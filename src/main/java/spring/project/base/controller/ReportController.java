package spring.project.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.request.SaleReportRequest;
import spring.project.base.dto.request.SaleReportResponse;
import spring.project.base.service.ISaleReportService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ISaleReportService saleReportService;

    public ReportController(ISaleReportService saleReportService) {
        this.saleReportService = saleReportService;
    }


    @Operation(summary = "Lấy bào cáo từ ngày A đến ngày B")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleReportResponse>>> getSaleReports(@RequestParam Instant fromDate, @RequestParam Instant toDate) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(this.saleReportService.getSaleReports(new SaleReportRequest(fromDate,toDate))));
    }
}
