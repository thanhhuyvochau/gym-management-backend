package spring.project.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import spring.project.base.common.ApiPage;
import spring.project.base.dto.request.AddMemberRequest;
import spring.project.base.dto.request.RegisterGymPlanRequest;
import spring.project.base.dto.request.UpdateMemberRequest;
import spring.project.base.dto.response.AttendanceResponse;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.entity.Member;

import java.time.Instant;

public interface IMemberService {
    ApiPage<MemberResponse> getAllMembers(Pageable pageable, String q);

    boolean addMember(AddMemberRequest request) throws JsonProcessingException;

    boolean editMember(Long id, UpdateMemberRequest request);

    MemberResponse findById(Long id);

    Boolean deleteMemberById(Long id);

    boolean extendMemberPlan(Long memberId, RegisterGymPlanRequest request);

    AttendanceResponse attendance(Resource memberFace) throws JsonProcessingException;

    Instant getCurrentActiveGymPlanExpiredDate(Member member);
}