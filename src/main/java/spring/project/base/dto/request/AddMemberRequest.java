package spring.project.base.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.constant.EGenderType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AddMemberRequest {
    private String fullName = "";
    private EGenderType gender = EGenderType.MALE;
    private String phoneNumber = "";
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Instant birthday;
    @NotNull
    private MultipartFile image;
    @Nullable
    private Long gymPlanId;
    @Nullable
    private BigDecimal actualPrice = BigDecimal.ZERO;
    @Nullable
    private Instant fromDate;
}
