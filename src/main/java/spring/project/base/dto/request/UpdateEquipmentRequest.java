package spring.project.base.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class UpdateEquipmentRequest {
    @NotNull
    private String code;
    @NotNull
    private String name;
    private Instant expectedDateFrom;
    private Instant expectedDateTo;
    private BigDecimal costPer = BigDecimal.ZERO;
    @NotNull
    private int quantity = 0;
    @Nullable
    private MultipartFile image;
    private boolean status;
}
