package spring.project.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiPage;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.request.AddMemberRequest;
import spring.project.base.dto.request.RegisterGymPlanRequest;
import spring.project.base.dto.request.UpdateMemberRequest;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.service.IMemberService;


@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final IMemberService memberService;

    public MemberController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "Thêm mới thành viên")
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> addNewMember(@ModelAttribute AddMemberRequest request) throws JsonProcessingException {
        boolean result = memberService.addMember(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "Lấy tất cả thành viên của chủ phòng gym")
    @GetMapping
    public ResponseEntity<ApiResponse<ApiPage<MemberResponse>>> getMembers(Pageable pageable, @Nullable @RequestParam String q) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(memberService.getAllMembers(pageable, q)));
    }

    @Operation(summary = "Cập nhật thông tin thành viên")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> editMember(@PathVariable("id") Long id,
                                                           @ModelAttribute UpdateMemberRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(memberService.editMember(id, request)));
    }

    @Operation(summary = "Xóa thành viên")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMember(@PathVariable("id") Long id) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(memberService.deleteMemberById(id)));
    }

    @Operation(summary = "Gia hạn thành viên")
    @PutMapping("/{id}/plan")
    public ResponseEntity<ApiResponse<Boolean>> extendGymPlan(@PathVariable("id") Long id, @RequestBody RegisterGymPlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.extendMemberPlan(id, request)));
    }
}
