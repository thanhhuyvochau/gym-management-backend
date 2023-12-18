package spring.project.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.base.entity.common.ApiException;
import spring.project.base.entity.common.ApiResponse;
import spring.project.base.entity.constant.EVerifyStatus;
import spring.project.base.entity.request.JwtResponse;
import spring.project.base.entity.request.LoginRequest;
import spring.project.base.entity.response.VerifyResponse;
import spring.project.base.service.IAuthService;
import spring.project.base.service.IUserService;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService iAuthService;
    private final IUserService userService;

    public AuthController(IAuthService iAuthService, IUserService userService) {
        this.iAuthService = iAuthService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse.success(iAuthService.userLogin(loginRequest)));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyResponse>> verifyAccount(@RequestParam("code") String code) {
        VerifyResponse response = userService.verifyAccount(code);
        if (!Objects.equals(response.getStatus(), EVerifyStatus.SUCCESS.name())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage(response.getMessage());
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/resend-verify")
    public ResponseEntity<ApiResponse<Boolean>> resendVerifyEmail() {
        return ResponseEntity.ok(ApiResponse.success(userService.resendVerifyEmail()));
    }
}

