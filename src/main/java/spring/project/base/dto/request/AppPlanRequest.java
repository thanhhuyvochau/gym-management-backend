package spring.project.base.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.project.base.constant.TimeUnit;
import spring.project.base.entity.Account;
import spring.project.base.entity.AppPlan;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppPlanRequest {
    private String name;
    private String description;
    private TimeUnit timeUnit;
    private Integer timeAmount;
    private BigDecimal price;
    private boolean activate;

    public static AppPlan mapToEntity(AppPlanRequest requestDTO, Account manager) {
        AppPlan appPlan = new AppPlan();
        appPlan.setName(requestDTO.getName());
        appPlan.setDescription(requestDTO.getDescription());
        appPlan.setTimeUnit(requestDTO.getTimeUnit());
        appPlan.setTimeAmount(requestDTO.getTimeAmount());
        appPlan.setPrice(requestDTO.getPrice());
        appPlan.setActivate(requestDTO.isActivate());
        appPlan.setManager(manager);
        return appPlan;
    }
}