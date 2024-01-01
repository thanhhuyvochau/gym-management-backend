package spring.project.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import spring.project.base.payment.constant.ETransactionStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private String description;
    @Enumerated(EnumType.STRING)
    private ETransactionStatus status;
    @ManyToOne
    @JoinColumn(name = "gym_owner_id")
    private Account gymOwner;
    @ManyToOne
    @JoinColumn(name = "app_plan")
    private AppPlan appPlan;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "vpn_command")
    private String vpnCommand;
    @Column(name = "order_info")
    private String orderInfo;
}
