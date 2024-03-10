package spring.project.base.dto.request;

import lombok.Data;

@Data
public class ForgetPasswordChange {
    private String otpCode;
    private String email;
    private String password;
}
