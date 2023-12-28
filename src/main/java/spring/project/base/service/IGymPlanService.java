package spring.project.base.service;

import spring.project.base.dto.request.GymPlanRequest;
import spring.project.base.dto.response.GymPlanResponse;

import java.util.List;

public interface IGymPlanService {
    GymPlanResponse createGymPlan(GymPlanRequest requestDTO);

    GymPlanResponse getGymPlan(Long id);

    List<GymPlanResponse> getAllGymPlans();

    GymPlanResponse updateGymPlan(Long id, GymPlanRequest requestDTO);

    void deleteGymPlan(Long id);
}
