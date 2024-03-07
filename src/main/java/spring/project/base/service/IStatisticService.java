package spring.project.base.service;

import spring.project.base.dto.request.StatisticRequest;
import spring.project.base.dto.response.RevenueStatisticResponse;

import java.util.List;

public interface IStatisticService {
    List<RevenueStatisticResponse> getRevenueStatistic(StatisticRequest request);
}
