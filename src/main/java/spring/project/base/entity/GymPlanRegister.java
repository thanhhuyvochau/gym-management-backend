package spring.project.base.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "gym_plan_register")
public class GymPlanRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "gym_plan_id")
    private GymPlan gymPlan;
    private Instant fromDate;
    private Instant toDate;
    private BigDecimal actualPrice = BigDecimal.ZERO;
}
