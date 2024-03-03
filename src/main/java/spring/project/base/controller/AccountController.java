package spring.project.base.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiPage;
import spring.project.base.common.ApiResponse;
import spring.project.base.constant.EAccountRole;
import spring.project.base.dto.request.AccountFilterRequest;
import spring.project.base.dto.request.ChangePasswordRequest;
import spring.project.base.dto.request.RegisterAccountRequest;
import spring.project.base.dto.request.UpdateAccountRequest;
import spring.project.base.dto.response.UserResponse;
import spring.project.base.service.IAccountService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final IAccountService iUserService;

    public AccountController(IAccountService iUserService) {
        this.iUserService = iUserService;
    }


    @Operation(summary = "Lấy thông tin user đang đăng nhập hiện tại")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentLoginUser() {
        return ResponseEntity.ok(ApiResponse.success(iUserService.getLoginUser()));
    }


    @Operation(summary = "Thay đổi mật khẩu")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Long>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.changePassword(changePasswordRequest)));
    }


    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản")
    public ResponseEntity<ApiResponse<Long>> registerAccount(@Valid @RequestBody RegisterAccountRequest createAccountRequest) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.registerAccount(createAccountRequest)));
    }

    @Operation(summary = "Lấy tất cả quản lý")
    @GetMapping("/managers")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<ApiPage<UserResponse>>> getManagerAccounts(@RequestParam(required = false) AccountFilterRequest request,
                                                                                 Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.getAccountsWithFilter(request, EAccountRole.MANAGER,
                pageable)));
    }

    @Operation(summary = "Lấy tất cả chủ phòng gym")
    @GetMapping("/gym-owners")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_GYM_OWNER')")
    public ResponseEntity<ApiResponse<ApiPage<UserResponse>>> getGymOwnerAccounts(@RequestParam(required = false) AccountFilterRequest request,
                                                                                  Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.getAccountsWithFilter(request, EAccountRole.GYM_OWNER,
                pageable)));
    }

    @Operation(summary = "Cập nhật hồ sơ người dùng")
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_GYM_OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@ModelAttribute UpdateAccountRequest request) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(iUserService.updateAccountProfile(request)));
    }
}