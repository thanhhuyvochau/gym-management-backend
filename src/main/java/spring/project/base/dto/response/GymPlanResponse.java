package spring.project.base.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spring.project.base.constant.TimeUnit;
import spring.project.base.entity.BaseEntity;
import spring.project.base.entity.GymPlan;
import spring.project.base.util.mapper.ObjectUtil;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class GymPlanResponse extends BaseEntity {
    private Long id;
    private String name;
    private String description;
    private Integer timeAmount;
    private TimeUnit timeUnit;
    private BigDecimal price;
    private Boolean activate;
    private Long gymOwnerId;
    private int numberOfRegister = 0;

    public static GymPlanResponse mapFromEntity(GymPlan gymPlan) {
        GymPlanResponse responseDTO = ObjectUtil.copyProperties(gymPlan, new GymPlanResponse(), GymPlanResponse.class);
        responseDTO.setGymOwnerId(gymPlan.getGymOwner().getId());
        responseDTO.numberOfRegister = gymPlan.getRegisterList().size();
        return responseDTO;
    }
}