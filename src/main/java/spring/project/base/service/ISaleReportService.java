package spring.project.base.service;

import spring.project.base.dto.request.SaleReportRequest;
import spring.project.base.dto.request.SaleReportResponse;

import java.util.List;

public interface ISaleReportService {
    List<SaleReportResponse> getSaleReports(SaleReportRequest saleReportRequest);
}
