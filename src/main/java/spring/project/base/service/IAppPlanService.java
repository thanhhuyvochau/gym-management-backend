package spring.project.base.service;

import spring.project.base.dto.request.AppPlanRequest;
import spring.project.base.dto.response.AppPlanResponse;

import java.util.List;

public interface IAppPlanService {
    AppPlanResponse createAppPlan(AppPlanRequest requestDTO);

    AppPlanResponse getAppPlan(Long id);

    List<AppPlanResponse> getAllAppPlans();

    AppPlanResponse updateAppPlan(Long id, AppPlanRequest requestDTO);

    void deleteAppPlan(Long id);
}
