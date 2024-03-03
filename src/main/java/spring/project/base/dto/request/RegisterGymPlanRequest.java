package spring.project.base.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class RegisterGymPlanRequest {
    Long gymPlanId;
    BigDecimal actualPrice;
    Instant fromDate;
}
