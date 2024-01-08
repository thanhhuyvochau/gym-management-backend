package spring.project.base.dto.request;
import lombok.Data;
import spring.project.base.constant.EGenderType;

@Data
public class AccountFilterRequest {
    private Long id;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private Boolean status;
    private EGenderType gender;
}
