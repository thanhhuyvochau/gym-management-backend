package spring.project.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.*;
import spring.project.base.dto.response.MemberResponse;

import java.util.List;

public interface IMemberService {
    ApiPage<MemberResponse> getAllMembers(Pageable pageable, String q);

    boolean addMember(AddMemberRequest request) throws JsonProcessingException;

    boolean editMember(Long id, UpdateMemberRequest request);

    MemberResponse findById(Long id);

    Boolean deleteMemberById(Long id);

    boolean extendMemberPlan(Long memberId, RegisterGymPlanRequest request);

}