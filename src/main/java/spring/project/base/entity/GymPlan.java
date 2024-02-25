package spring.project.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spring.project.base.constant.TimeUnit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gym_plan")
@Data
public class GymPlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "time_amount")
    private Integer timeAmount;
    @Column(name = "time_unit")
    private TimeUnit timeUnit;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "activate")
    private Boolean activate = false;
    @ManyToOne
    @JoinColumn(name = "gym_owner_id")
    private Account gymOwner;
    @OneToMany(mappedBy = "gymPlan", cascade = CascadeType.ALL)
    private List<GymPlanRegister> registerList = new ArrayList<>();
}
