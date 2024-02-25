package spring.project.base.dto.request;


import lombok.Data;
import spring.project.base.constant.TimeUnit;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlan;
import spring.project.base.util.mapper.ObjectUtil;

import java.math.BigDecimal;

@Data
public class GymPlanRequest {
    private String name;
    private String description;
    private Integer timeAmount;
    private TimeUnit timeUnit;
    private BigDecimal price;
    private Boolean activate;

    public static GymPlan mapToEntity(GymPlanRequest requestDTO, Account gymOwner) {
        GymPlan gymPlan = ObjectUtil.copyProperties(requestDTO, new GymPlan(), GymPlan.class);
        gymPlan.setGymOwner(gymOwner);  // Set GymOwner using the provided parameter
        return gymPlan;
    }
}
