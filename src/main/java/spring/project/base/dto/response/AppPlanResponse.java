package spring.project.base.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.project.base.constant.TimeUnit;
import spring.project.base.entity.AppPlan;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppPlanResponse {
    private Long id;
    private String name;
    private String description;
    private TimeUnit timeUnit;
    private Integer timeAmount;
    private BigDecimal price;
    private boolean activate;
    private Long managerId;

    public static AppPlanResponse mapFromEntity(AppPlan appPlan) {
        AppPlanResponse responseDTO = new AppPlanResponse();
        responseDTO.setId(appPlan.getId());
        responseDTO.setName(appPlan.getName());
        responseDTO.setDescription(appPlan.getDescription());
        responseDTO.setTimeUnit(appPlan.getTimeUnit());
        responseDTO.setTimeAmount(appPlan.getTimeAmount());
        responseDTO.setPrice(appPlan.getPrice());
        responseDTO.setActivate(appPlan.isActivate());
        responseDTO.setManagerId(appPlan.getManager().getId());
        return responseDTO;
    }
}