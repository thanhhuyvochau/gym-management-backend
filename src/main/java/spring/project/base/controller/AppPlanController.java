package spring.project.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.project.base.dto.request.AppPlanRequest;
import spring.project.base.dto.response.AppPlanResponse;
import spring.project.base.service.IAppPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/app-plans")
@Secured("has")
public class AppPlanController {
    private final IAppPlanService appPlanService;

    public AppPlanController(IAppPlanService appPlanService) {
        this.appPlanService = appPlanService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppPlanResponse> createAppPlan(@RequestBody AppPlanRequest requestDTO) {
        AppPlanResponse createdAppPlan = appPlanService.createAppPlan(requestDTO);
        return ResponseEntity.ok(createdAppPlan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppPlanResponse> getAppPlan(@PathVariable Long id) {
        AppPlanResponse appPlan = appPlanService.getAppPlan(id);
        return ResponseEntity.ok(appPlan);
    }

    @GetMapping
    public ResponseEntity<List<AppPlanResponse>> getAllAppPlans() {
        List<AppPlanResponse> allAppPlans = appPlanService.getAllAppPlans();
        return ResponseEntity.ok(allAppPlans);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppPlanResponse> updateAppPlan(@PathVariable Long id,
                                                         @RequestBody AppPlanRequest requestDTO) {
        AppPlanResponse updatedAppPlan = appPlanService.updateAppPlan(id, requestDTO);
        return ResponseEntity.ok(updatedAppPlan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppPlan(@PathVariable Long id) {
        appPlanService.deleteAppPlan(id);
        return ResponseEntity.noContent().build();
    }
}
