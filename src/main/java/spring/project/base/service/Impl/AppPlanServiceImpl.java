package spring.project.base.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import spring.project.base.common.ApiException;
import spring.project.base.dto.request.AppPlanRequest;
import spring.project.base.dto.response.AppPlanResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.AppPlan;
import spring.project.base.repository.AppPlanRepository;
import spring.project.base.service.IAccountService;
import spring.project.base.service.IAppPlanService;
import spring.project.base.util.account.SecurityUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppPlanServiceImpl implements IAppPlanService {
    private final AppPlanRepository appPlanRepository;
    private final IAccountService accountService;

    public AppPlanServiceImpl(AppPlanRepository appPlanRepository, IAccountService accountService) {
        this.appPlanRepository = appPlanRepository;
        this.accountService = accountService;
    }

    @Override
    public AppPlanResponse createAppPlan(AppPlanRequest requestDTO) {
        Account manager = SecurityUtil.getCurrentUser();
        AppPlan appPlan = AppPlanRequest.mapToEntity(requestDTO, manager);
        AppPlan savedAppPlan = appPlanRepository.save(appPlan);
        return AppPlanResponse.mapFromEntity(savedAppPlan);
    }

    @Override
    public AppPlanResponse getAppPlan(Long id) {
        AppPlan appPlan = appPlanRepository.findById(id)
                .orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND).withMessage("AppPlan not found with id: "
                        + id));
        return AppPlanResponse.mapFromEntity(appPlan);
    }

    @Override
    public List<AppPlanResponse> getAllAppPlans() {
        List<AppPlan> allAppPlans = appPlanRepository.findAll();
        return allAppPlans.stream()
                .map(AppPlanResponse::mapFromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AppPlanResponse updateAppPlan(Long id, AppPlanRequest requestDTO) {
        Account manager = SecurityUtil.getCurrentUser();
        AppPlan appPlan = appPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppPlan not found with id: " + id));

        // Update fields from requestDTO
        appPlan.setName(requestDTO.getName());
        appPlan.setDescription(requestDTO.getDescription());
        appPlan.setTimeUnit(requestDTO.getTimeUnit());
        appPlan.setTimeAmount(requestDTO.getTimeAmount());
        appPlan.setPrice(requestDTO.getPrice());
        appPlan.setActivate(requestDTO.isActivate());
        appPlan.setManager(manager);

        AppPlan updatedAppPlan = appPlanRepository.save(appPlan);
        return AppPlanResponse.mapFromEntity(updatedAppPlan);
    }

    @Override
    public void deleteAppPlan(Long id) {
        appPlanRepository.deleteById(id);
    }
}
