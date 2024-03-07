package spring.project.base.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@AllArgsConstructor
public class StatisticRequest {
    @NotNull
    private Instant fromMonth;
    @NotNull
    private Instant toMonth;
}
