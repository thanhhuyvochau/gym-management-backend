// GymPlanController.java
package spring.project.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.request.GymPlanRequest;
import spring.project.base.dto.response.GymPlanResponse;
import spring.project.base.service.IGymPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/gym-plans")
public class GymPlanController {

    private final IGymPlanService gymPlanService;

    public GymPlanController(IGymPlanService gymPlanService) {
        this.gymPlanService = gymPlanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GymPlanResponse>> createGymPlan(@RequestBody GymPlanRequest requestDTO) {
        GymPlanResponse createdGymPlan = gymPlanService.createGymPlan(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(createdGymPlan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GymPlanResponse>> getGymPlan(@PathVariable Long id) {
        GymPlanResponse gymPlan = gymPlanService.getGymPlan(id);
        return ResponseEntity.ok(ApiResponse.success(gymPlan));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GymPlanResponse>>> getAllGymPlans() {
        List<GymPlanResponse> allGymPlans = gymPlanService.getAllGymPlans();
        return ResponseEntity.ok(ApiResponse.success(allGymPlans));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GymPlanResponse>> updateGymPlan(@PathVariable Long id,
                                                                      @RequestBody GymPlanRequest requestDTO) {
        GymPlanResponse updatedGymPlan = gymPlanService.updateGymPlan(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedGymPlan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGymPlan(@PathVariable Long id) {
        gymPlanService.deleteGymPlan(id);
        return ResponseEntity.noContent().build();
    }
}
