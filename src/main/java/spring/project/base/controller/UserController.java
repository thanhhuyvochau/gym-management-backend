package spring.project.base.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.base.entity.common.ApiResponse;
import spring.project.base.entity.dto.UserDto;
import spring.project.base.entity.request.ChangePasswordRequest;
import spring.project.base.entity.request.RegisterAccountRequest;
import spring.project.base.service.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService iUserService;

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }


    @Operation(summary = "Lấy thông tin user đang đăng nhập hiện tại")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentLoginUser() {
        return ResponseEntity.ok(ApiResponse.success(iUserService.getLoginUser()));
    }


    @Operation(summary = "Thay đổi mật khẩu")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Long>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.changePassword(changePasswordRequest)));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerAccount(@Valid @RequestBody RegisterAccountRequest createAccountRequest) {
        return ResponseEntity.ok(ApiResponse.success(iUserService.registerAccount(createAccountRequest)));
    }

}