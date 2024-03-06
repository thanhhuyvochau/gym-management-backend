package spring.project.base.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SaleReportRequest {
    private Instant fromDate;
    private Instant toDate;
}
