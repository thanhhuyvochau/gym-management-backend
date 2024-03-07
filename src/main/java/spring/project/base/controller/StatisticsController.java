package spring.project.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.request.SaleReportRequest;
import spring.project.base.dto.request.SaleReportResponse;
import spring.project.base.dto.request.StatisticRequest;
import spring.project.base.dto.response.RevenueStatisticResponse;
import spring.project.base.service.IStatisticService;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final IStatisticService statisticService;

    public StatisticsController(IStatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Operation(summary = "Lấy thống kê từ tháng A đến tháng B")
    @GetMapping("/revenues")
    public ResponseEntity<ApiResponse<List<RevenueStatisticResponse>>> getRevenues(@RequestParam @NotNull Instant fromDate, @RequestParam @NotNull Instant toDate) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(this.statisticService.getRevenueStatistic(new StatisticRequest(fromDate, toDate))));
    }
//    @Operation(summary = "Lấy thống kê từ tháng A đến tháng B")
//    @GetMapping("/registers")
//    public ResponseEntity<ApiResponse<List<RevenueStatisticResponse>>> getRevenues(@RequestParam @NotNull Instant fromDate, @RequestParam @NotNull Instant toDate) throws JsonProcessingException {
//        return ResponseEntity.ok(ApiResponse.success(this.statisticService.getRevenueStatistic(new StatisticRequest(fromDate, toDate))));
//    }
}
