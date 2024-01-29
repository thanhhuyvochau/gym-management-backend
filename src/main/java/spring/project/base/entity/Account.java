package spring.project.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import spring.project.base.constant.EGenderType;
import spring.project.base.constant.SocialProvider;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "account")
@Data
public class Account extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @JsonIgnore
    @Column(name = "password")
    private String password;
    @Column(name = "address")
    private String address;
    @Column(name = "status")
    private boolean status = false;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
    @Column(name = "verified")
    private boolean verified = false;
    @Column(name = "provider")
    private SocialProvider provider = SocialProvider.LOCAL;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private EGenderType gender;
    @OneToMany(mappedBy = "gymOwner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GymPlan> gymPlans = new ArrayList<>();
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AppPlan> appPlans = new ArrayList<>();
    @OneToMany(mappedBy = "gymOwner", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH,
            CascadeType.DETACH})
    private List<Transaction> transactions = new ArrayList<>();
    @OneToMany(mappedBy = "gymOwner", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();
}
