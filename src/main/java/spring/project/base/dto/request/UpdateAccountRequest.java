package spring.project.base.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.constant.EGenderType;

@Data
public class UpdateAccountRequest {
    private String fullName;
    private String phone;
    private EGenderType gender;
    private String address;
    @Nullable
    private MultipartFile image;
}
