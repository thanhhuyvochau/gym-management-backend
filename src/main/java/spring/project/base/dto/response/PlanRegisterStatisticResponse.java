package spring.project.base.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PlanRegisterStatisticResponse {
    private Long id;
    private String name;
    private int numberOfRegister = 0;
}
