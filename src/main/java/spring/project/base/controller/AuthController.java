package spring.project.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiResponse;
import spring.project.base.constant.EVerifyStatus;
import spring.project.base.dto.request.JwtResponse;
import spring.project.base.dto.request.LoginRequest;
import spring.project.base.dto.response.VerifyResponse;
import spring.project.base.service.IAccountService;
import spring.project.base.service.IAuthService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final IAuthService iAuthService;
    private final IAccountService userService;

    public AuthController(IAuthService iAuthService, IAccountService userService) {
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

