package spring.project.base.dto.request;

import lombok.Data;

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
}