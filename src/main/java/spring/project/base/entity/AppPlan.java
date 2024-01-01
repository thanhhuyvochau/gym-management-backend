package spring.project.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import spring.project.base.constant.TimeUnit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AppPlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "time_unit")
    private TimeUnit timeUnit;
    @Column(name = "time_amount")
    private Integer timeAmount;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "activate")
    private boolean activate;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Account manager;
    @OneToMany(mappedBy = "appPlan", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH,
            CascadeType.DETACH})
    private List<Transaction> transactions = new ArrayList<>();
}
