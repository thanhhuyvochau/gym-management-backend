package spring.project.base.dto.request;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import javax.validation.constraints.NotNull;

@Data
public class RegisterAccountRequest {
    @NotNull
    private String fullName;
    @NotNull
    private String email;
    @NotNull
    private String phone;
    @NotNull
    private String password;
    @NotNull
    private EGenderType gender;
    @NotNull
    private String address;
}
