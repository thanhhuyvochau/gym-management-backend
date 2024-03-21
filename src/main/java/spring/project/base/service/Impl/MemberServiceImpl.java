package spring.project.base.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.ObjectWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.other.MemberData;
import spring.project.base.dto.other.RecognitionResult;
import spring.project.base.dto.other.ResultEntry;
import spring.project.base.dto.request.AddMemberRequest;
import spring.project.base.dto.request.RegisterGymPlanRequest;
import spring.project.base.dto.request.UpdateMemberRequest;
import spring.project.base.dto.response.AttendanceResponse;
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
import spring.project.base.validator.GymPlanValidator;
import spring.project.base.validator.MemberValidator;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    @Value("${detection.recogize-url}")
    private String checkFaceURL;
    @Value("${detection.create-url}")
    private String createFaceURL;

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
                    memberResponse.setPlanName(firstGymPlanRegisterOfMember.getGymPlan().getName());
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
            memberRepository.save(member);

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
                if (!sendImageToFaceRecognizeSystemForCreate(file.getResource(), member.getId())) {
                    throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Update image to face system failed !!");
                }
            }
            String encrypt = EncryptionUtils.encrypt(objectMapper.writeValueAsString(memberData));
            member.setEncodeMemberData(encrypt);

            if (request.getPlan() != null) {
                Long gymPlanId = request.getPlan();
                GymPlan gymPlan = gymPlanRepository.findById(gymPlanId)
                        .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found gym plan with id:" + gymPlanId));
                regisGymPlan(member, gymPlanId, request.getFromDate(), request.getActualPrice(), gymOwner, gymPlan);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
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
                if (!sendImageToFaceRecognizeSystemForCreate(file.getResource(), member.getId())) {
                    throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Update image to face system failed !!");
                }
            }

            member.setEncodeMemberData(EncryptionUtils.encrypt(objectMapper.writeValueAsString(memberData)));
            memberRepository.save(member);
            if (request.getGymPlanId() != null) {
                GymPlan gymPlan = gymPlanRepository.findById(request.getGymPlanId())
                        .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found gym plan with id:" + request.getGymPlanId()));
                regisGymPlan(member, request.getGymPlanId(), request.getFromDate(), request.getActualPrice(), gymOwner, gymPlan);
            }
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
        GymPlan gymPlan = gymPlanRepository.findById(request.getGymPlanId())
                .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found gym plan with id:" + request.getGymPlanId()));
        regisGymPlan(member, request.getGymPlanId(), request.getFromDate(), request.getActualPrice(), gymOwner, gymPlan);
        return true;
    }

    private void regisGymPlan(Member member, Long gymPlanId, Instant fromDate, BigDecimal actualPrice, Account gymOwner, GymPlan gymPlan) {
        if (gymPlanId != null && fromDate != null) {
            Instant currentActiveGymPlanToDate = this.getCurrentActiveGymPlanExpiredDate(member);
            if (currentActiveGymPlanToDate != null && currentActiveGymPlanToDate.isAfter(fromDate)) {
                throw ApiException.create(HttpStatus.PRECONDITION_FAILED).withMessage("From date should after expired date of current active plan:" + currentActiveGymPlanToDate);
            } else if (!GymPlanValidator.isGymOwnerOfGymPlan(gymOwner, gymPlan)) {
                throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner dont own this plan, Please try again!!");
            }

            GymPlanRegister gymPlanRegister = new GymPlanRegister();
            gymPlanRegister.setMember(member);
            gymPlanRegister.setActualPrice(actualPrice == null ? gymPlan.getPrice() : actualPrice);
            gymPlanRegister.setFromDate(fromDate);
            gymPlanRegister.setToDate(GymPlanUtil.calculateToDateOfPlan(fromDate, gymPlan.getTimeUnit().getDayNumber()));
            gymPlanRegister.setGymPlan(gymPlan);
            gymPlanRegisterRepository.save(gymPlanRegister);
        }
    }

    @Override
    public Instant getCurrentActiveGymPlanExpiredDate(Member member) {
        GymPlanRegister currentActivePlan = getFirstGymPlanRegisterOfMember(member);
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

    @Override
    public AttendanceResponse attendance(Resource memberFace) throws JsonProcessingException {
        Account gymOwner = SecurityUtil.getCurrentUser();
        List<ResultEntry> result = sendImageToFaceRecognizeSystemForDetect(memberFace);

        AttendanceResponse attendanceResponse = new AttendanceResponse();
        if (!result.isEmpty()) {
            Optional<ResultEntry> firstMatch = result.stream().filter(r -> !r.getLabel().equals("unknown")).findFirst();
            if (firstMatch.isPresent()) {
                ResultEntry resultEntry = (ResultEntry) firstMatch.get();
                String label = resultEntry.getLabel();

                long memberId = Integer.parseInt(label);
                Member detectMember = memberRepository.findByIdAndGymOwner_Id(memberId, gymOwner.getId())
                        .orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Not found member for detect with id:" + memberId));
                // Find active plan of member or recent plan of member
                // If member has active plan return true and if not return false
                Instant currentActiveGymPlanExpiredDate = getCurrentActiveGymPlanExpiredDate(detectMember);
                if (currentActiveGymPlanExpiredDate != null) {
                    attendanceResponse.setStatus(true);
                    attendanceResponse.setMessage("Detect successfully!!");
                } else {
                    attendanceResponse.setStatus(false);
                    attendanceResponse.setMessage("Member ship of member is expired!!");
                }
            } else {
                attendanceResponse.setStatus(false);
                attendanceResponse.setMessage("Detect fail!!");
            }
        } else {
            attendanceResponse.setStatus(false);
            attendanceResponse.setMessage("Detect fail!!");
        }
        return attendanceResponse;
    }

    private List<ResultEntry> sendImageToFaceRecognizeSystemForDetect(Resource memberFace) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        // Set up request headers and body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", memberFace); // Use the same key as expected by the server
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // Replace with the target system's endpoint URL
        String targetUrl = checkFaceURL;
        // Send the request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, requestEntity, String.class);
        RecognitionResult recognitionResult = objectMapper.readValue(responseEntity.getBody(), RecognitionResult.class);
        List<ResultEntry> result = recognitionResult.getResult();
        return result;
    }

    private boolean sendImageToFaceRecognizeSystemForCreate(Resource memberFace, long memberId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", memberFace); // Use the same key as expected by the server
        body.add("label", memberId);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String targetUrl = createFaceURL;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, requestEntity, String.class);
        return responseEntity.getStatusCodeValue() == 200;
    }
}
