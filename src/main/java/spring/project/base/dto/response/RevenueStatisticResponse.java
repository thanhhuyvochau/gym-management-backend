package spring.project.base.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class RevenueStatisticResponse {
    Instant date;
    BigDecimal revenue;
}
