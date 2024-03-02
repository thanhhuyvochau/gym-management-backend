// GymPlanServiceImpl.java
package spring.project.base.service.Impl;

import org.springframework.stereotype.Service;
import spring.project.base.dto.request.GymPlanRequest;
import spring.project.base.dto.response.GymPlanResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlan;
import spring.project.base.repository.GymPlanRepository;
import spring.project.base.service.IGymPlanService;
import spring.project.base.util.account.SecurityUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GymPlanServiceImpl implements IGymPlanService {

    private final GymPlanRepository gymPlanRepository;

    public GymPlanServiceImpl(GymPlanRepository gymPlanRepository) {
        this.gymPlanRepository = gymPlanRepository;
    }

    @Override
    public GymPlanResponse createGymPlan(GymPlanRequest requestDTO) {
        Account gymOwner = SecurityUtil.getCurrentUser();
        GymPlan gymPlan = GymPlanRequest.mapToEntity(requestDTO, gymOwner);
        GymPlan savedGymPlan = gymPlanRepository.save(gymPlan);
        return GymPlanResponse.mapFromEntity(savedGymPlan);
    }

    @Override
    public GymPlanResponse getGymPlan(Long id) {
        GymPlan gymPlan = gymPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GymPlan not found with id: " + id));
        return GymPlanResponse.mapFromEntity(gymPlan);
    }

    @Override
    public List<GymPlanResponse> getAllGymPlans() {
        List<GymPlan> allGymPlans = gymPlanRepository.findAll();
        return allGymPlans.stream()
                .map(GymPlanResponse::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public GymPlanResponse updateGymPlan(Long id, GymPlanRequest requestDTO) {
        GymPlan gymPlan = gymPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GymPlan not found with id: " + id));

        // Update fields from requestDTO
        gymPlan.setName(requestDTO.getName());
        gymPlan.setDescription(requestDTO.getDescription());
        gymPlan.setTimeAmount(requestDTO.getTimeAmount());
        gymPlan.setTimeUnit(requestDTO.getTimeUnit());
        gymPlan.setPrice(requestDTO.getPrice());
        gymPlan.setActivate(requestDTO.getActivate());

        GymPlan updatedGymPlan = gymPlanRepository.save(gymPlan);
        return GymPlanResponse.mapFromEntity(updatedGymPlan);
    }

    @Override
    public void deleteGymPlan(Long id) {
        gymPlanRepository.deleteById(id);
    }
}
