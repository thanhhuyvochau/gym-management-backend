package spring.project.base.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.RegisterGymPlanRequest;
import spring.project.base.dto.request.UpdateMemberRequest;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.Member;
import spring.project.base.repository.MemberRepository;
import spring.project.base.service.IMemberService;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.mapper.ConvertUtil;
import spring.project.base.util.mapper.PageUtil;
import spring.project.base.util.other.EncryptionUtils;
import spring.project.base.validator.MemberValidator;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class MemberServiceImpl implements IMemberService {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    public MemberServiceImpl(MemberRepository memberRepository, ObjectMapper objectMapper) {
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
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
                return memberResponse;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public boolean addMember(UpdateMemberRequest request) {
        Member member = new Member();
        Account gymOwner = SecurityUtil.getCurrentUser();
        try {
            member.setEncodeMemberData(EncryptionUtils.encrypt(objectMapper.writeValueAsString(request)));
            member.setGymOwner(gymOwner);
            memberRepository.save(member);
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
            member.setEncodeMemberData(EncryptionUtils.encrypt(objectMapper.writeValueAsString(request)));
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
    public boolean extendMemberPlan(Long memberId, RegisterGymPlanRequest request) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        Member member = this.memberRepository.findById(memberId).orElseThrow(() -> ApiException.create(HttpStatus.BAD_REQUEST)
                .withMessage("Not found member with id:" + memberId));
        String decrypt = member.getEncodeMemberData();
        if (!MemberValidator.isGymOwnerOfMember(gymOwner, member)) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage("Gym owner can not edits this member!");
        }

        return false;
    }
}