package spring.project.base.dto.request;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class UpdateAccountRequest {
    private String fullName;
    private String phone;
    private EGenderType gender;
    private String address;
}
