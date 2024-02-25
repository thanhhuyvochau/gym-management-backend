package spring.project.base.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.constant.EGenderType;

import java.time.LocalDateTime;

@Data
public class UpdateMemberRequest {
    private Integer age;
    private String fullName = "";
    private EGenderType gender = EGenderType.MALE;
    private String phoneNumber = "";
    private LocalDateTime birthday;
    @Nullable
    private MultipartFile image;
}
