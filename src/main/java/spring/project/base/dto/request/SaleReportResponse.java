package spring.project.base.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.project.base.dto.response.GenderResponse;
import spring.project.base.dto.response.MemberResponse;
import spring.project.base.entity.GymPlan;
import spring.project.base.entity.GymPlanRegister;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class SaleReportResponse {
    private String memberName;
    private GenderResponse gender;
    private String planName;
    private Instant paidDate;
    private BigDecimal amount;

    public static SaleReportResponse convertDtoToSaleReport(MemberResponse member, GymPlan gymPlan, GymPlanRegister register) {
        GenderResponse genderResponse = new GenderResponse(member.getGender().getLabel(), member.getGender().name());
        return new SaleReportResponse(member.getFullName(), genderResponse, gymPlan.getName(), register.getCreated(), register.getActualPrice());
    }
}
