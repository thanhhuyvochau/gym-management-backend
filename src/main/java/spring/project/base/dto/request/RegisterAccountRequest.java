package spring.project.base.dto.request;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import javax.validation.constraints.NotNull;
import java.time.Instant;
@Data
public class RegisterAccountRequest {
//    @NotNull
//    private String fullName;
    @NotNull
    private String email;
//    @NotNull
//    private String phone;
    @NotNull
    private String password;
//    private EGenderType gender;
//    @NotNull
//    private Instant birthDay;
//    @NotNull
//    private String address;




}
