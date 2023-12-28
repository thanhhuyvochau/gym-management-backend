package spring.project.base.dto.response;

import lombok.Data;
import spring.project.base.constant.TimeUnit;
import spring.project.base.entity.GymPlan;
import spring.project.base.util.mapper.ObjectUtil;

import java.math.BigDecimal;

@Data
public class GymPlanResponse {
    private Long id;
    private String name;
    private String description;
    private Integer timeAmount;
    private TimeUnit timeUnit;
    private BigDecimal price;
    private Boolean activate;
    private Long gymOwnerId;

    public static GymPlanResponse mapFromEntity(GymPlan gymPlan) {
        GymPlanResponse responseDTO = ObjectUtil.copyProperties(gymPlan, new GymPlanResponse(), GymPlanResponse.class);
        responseDTO.setGymOwnerId(gymPlan.getGymOwner().getId());
        return responseDTO;
    }
}