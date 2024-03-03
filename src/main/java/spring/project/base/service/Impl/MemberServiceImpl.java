package spring.project.base.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.ObjectWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.other.MemberData;
import spring.project.base.dto.request.AddMemberRequest;
import spring.project.base.dto.request.RegisterGymPlanRequest;
import spring.project.base.dto.request.UpdateMemberRequest;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlan;
import spring.project.base.entity.GymPlanRegister;
import spring.project.base.entity.Member;
import spring.project.base.repository.GymPlanRegisterRepository;
import spring.project.base.repository.GymPlanRepository;
import spring.project.base.repository.MemberRepository;
import spring.project.base.service.IMemberService;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.adapter.MinioAdapter;
import spring.project.base.util.formater.MiniIOUtil;
import spring.project.base.util.mapper.PageUtil;
import spring.project.base.util.other.EncryptionUtils;
import spring.project.base.util.other.GymPlanUtil;
import spring.project.base.validator.MemberValidator;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class MemberServiceImpl implements IMemberService {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    private final MinioAdapter minioAdapter;
    @Value("${minio.endpoint}")
    private String minioUrl;
    private final GymPlanRegisterRepository gymPlanRegisterRepository;
    private final GymPlanRepository gymPlanRepository;

    public MemberServiceImpl(MemberRepository memberRepository, ObjectMapper objectMapper, MinioAdapter minioAdapter, GymPlanRegisterRepository gymPlanRegisterRepository, GymPlanRepository gymPlanRepository) {
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
        this.minioAdapter = minioAdapter;
        this.gymPlanRegisterRepository = gymPlanRegisterRepository;
        this.gymPlanRepository = gymPlanRepository;
    }

    @Override
    public ApiPage<MemberResponse> getAllMembers(Pageable pageable, String q) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Page<Member> allMembers = memberRepository.findAllByGymOwner_Id(gymOwner.getId(), pageable);
        return PageUtil.convert(allMembers.map(member -> {
            String encodeMemberData = member.getEncodeMemberData();
            try {
                String decrypt = EncryptionUtils.decrypt(encodeMemberData);
                MemberResponse memberResponse = objectMapper.readValue(decrypt, MemberResponse.class);
                memberResponse.setId(member.getId());
                GymPlanRegister firstGymPlanRegisterOfMember = this.getFirstGymPlanRegisterOfMember(member);
                if (firstGymPlanRegisterOfMember != null) {
                    memberResponse.setDateEnrolled(firstGymPlanRegisterOfMember.getFromDate());
                    memberResponse.setDateExpiration(firstGymPlanRegisterOfMember.getToDate());
                }
                return memberResponse;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public boolean addMember(AddMemberRequest request) {
        Member member = new Member();
        Account gymOwner = SecurityUtil.getCurrentUser();
        try {
            MemberData memberData = new MemberData();
            memberData.setBirthday(request.getBirthday());
            memberData.setGender(request.getGender());
            memberData.setFullName(request.getFullName());
            memberData.setPhoneNumber(request.getPhoneNumber());
            member.setGymOwner(gymOwner);

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                MultipartFile file = request.getImage();
                String name = request.getFullName() + "_" + Instant.now().toString();
                ObjectWriteResponse objectWriteResponse = minioAdapter.uploadFile(name, file.getContentType(), file.getInputStream(), file.getSize());
                String imageURL = MiniIOUtil.buildUrl(minioUrl, objectWriteResponse);
                memberData.setMemberImage(imageURL);
            }
            member.setEncodeMemberData(EncryptionUtils.encrypt(objectMapper.writeValueAsString(memberData)));
            memberRepository.save(member);

            // Register plan part while adding new member
            regisGymPlan(member, request.getGymPlanId(), request.getFromDate(), request.getActualPrice());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean editMember(Long id, UpdateMemberRequest request) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Member member = this.memberRepository.findById(id).orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST)
                .withMessage("Not found member with id:" + id));
        if (!MemberValidator.isGymOwnerOfMember(gymOwner, member)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner can not edits this member!");
        }
        try {

            MemberData memberData = new MemberData();
            memberData.setBirthday(request.getBirthday());
            memberData.setGender(request.getGender());
            memberData.setFullName(request.getFullName());
            memberData.setPhoneNumber(request.getPhoneNumber());
            member.setGymOwner(gymOwner);

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                MultipartFile file = request.getImage();
                String name = request.getFullName() + "_" + Instant.now().toString();
                ObjectWriteResponse objectWriteResponse = minioAdapter.uploadFile(name, file.getContentType(), file.getInputStream(), file.getSize());
                String imageURL = MiniIOUtil.buildUrl(minioUrl, objectWriteResponse);
                memberData.setMemberImage(imageURL);
            }

            member.setEncodeMemberData(EncryptionUtils.encrypt(objectMapper.writeValueAsString(memberData)));
            memberRepository.save(member);

            regisGymPlan(member, request.getGymPlanId(), request.getFromDate(), request.getActualPrice());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public MemberResponse findById(Long id) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Member member = this.memberRepository.findById(id).orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST)
                .withMessage("Not found member with id:" + id));
        String decrypt = member.getEncodeMemberData();
        if (!MemberValidator.isGymOwnerOfMember(gymOwner, member)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner can not edits this member!");
        }
        try {
            MemberResponse memberResponse = objectMapper.readValue(decrypt, MemberResponse.class);
            memberResponse.setId(member.getId());
            return memberResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ApiException.create(HttpStatus.INTERNAL_SERVER_ERROR).withMessage(e.getMessage());
        }
    }

    @Override
    public Boolean deleteMemberById(Long id) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found member with id:" + id));
        if (MemberValidator.isGymOwnerOfMember(gymOwner, member)) {
            memberRepository.delete(member);
        } else {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("User not gym owner of this member");
        }
        return true;
    }

    @Override
    public boolean extendMemberPlan(Long id, RegisterGymPlanRequest request) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Member member = this.memberRepository.findById(id).orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST)
                .withMessage("Not found member with id:" + id));
        if (!MemberValidator.isGymOwnerOfMember(gymOwner, member)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner can not edits this member!");
        }
        regisGymPlan(member, request.getGymPlanId(), request.getFromDate(), request.getActualPrice());
        return true;
    }

    private void regisGymPlan(Member member, Long gymPlanId, Instant fromDate, BigDecimal actualPrice) {
        if (gymPlanId != null && fromDate != null) {
            Instant currentActiveGymPlanToDate = this.getCurrentActiveGymPlanExpiredDate(member);
            if (currentActiveGymPlanToDate != null && currentActiveGymPlanToDate.isAfter(fromDate)) {
                throw ApiException.create(HttpStatus.PRECONDITION_FAILED).withMessage("From date should after expired date of current active plan:" + currentActiveGymPlanToDate);
            }
            GymPlan gymPlan = gymPlanRepository.findById(gymPlanId)
                    .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found gym plan with id:" + gymPlanId));
            GymPlanRegister gymPlanRegister = new GymPlanRegister();
            gymPlanRegister.setMember(member);
            gymPlanRegister.setActualPrice(actualPrice == null ? gymPlan.getPrice() : actualPrice);
            gymPlanRegister.setFromDate(fromDate);
            gymPlanRegister.setToDate(GymPlanUtil.calculateToDateOfPlan(fromDate, gymPlan.getTimeUnit().getDayNumber()));
            gymPlanRegister.setGymPlan(gymPlan);
            gymPlanRegisterRepository.save(gymPlanRegister);
        }
    }

    private Instant getCurrentActiveGymPlanExpiredDate(Member member) {
        GymPlanRegister currentActivePlan = this.gymPlanRegisterRepository.findByMemberAndToDateAfter(member, Instant.now());
        return currentActivePlan == null ? null : currentActivePlan.getToDate();
    }

    private GymPlanRegister getFirstGymPlanRegisterOfMember(Member member) {
        // Priority
        // 1 - Current Active Plan
        // 2 - Future Active Plan
        // 3 - Expired Plan
        Instant current = Instant.now();
        List<GymPlanRegister> registerList = member.getRegisterList();
        GymPlanRegister firstGymPlanRegister = null;
        for (GymPlanRegister gymPlanRegister : registerList) {

            if (current.isAfter(gymPlanRegister.getToDate()) && current.isBefore(gymPlanRegister.getFromDate())) { // Current Active Plan
                return gymPlanRegister;
            } else if (current.isBefore(gymPlanRegister.getFromDate())) { // Future Active Plan
                firstGymPlanRegister = gymPlanRegister;
            } else {
                firstGymPlanRegister = gymPlanRegister;
            }
        }
        return firstGymPlanRegister;
    }
}
