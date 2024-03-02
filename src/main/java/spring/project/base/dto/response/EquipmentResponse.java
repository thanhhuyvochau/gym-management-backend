package spring.project.base.dto.response;

import lombok.Data;
import spring.project.base.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EquipmentResponse extends BaseEntity {
    private String code;
    private String name;
    private Instant expectedDateFrom;
    private Instant expectedDateTo;
    private BigDecimal costPer = BigDecimal.ZERO;
    private int quantity = 0;
}
