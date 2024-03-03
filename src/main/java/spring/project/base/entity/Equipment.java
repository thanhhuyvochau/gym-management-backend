package spring.project.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "equipment")
public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String code;
    private String name;
    private Instant expectedDateFrom;
    private Instant expectedDateTo;
    private BigDecimal costPer = BigDecimal.ZERO;
    private boolean status = false;
    private int quantity = 0;
    @ManyToOne
    @JoinColumn(name = "gym_owner_id")
    private Account gymOwner;
    private String image;
}
