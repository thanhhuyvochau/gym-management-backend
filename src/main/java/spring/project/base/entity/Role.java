package spring.project.base.entity;


import spring.project.base.constant.EUserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "role")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    @Enumerated(EnumType.STRING)
    private EUserRole code;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "role")
    private List<Account> accounts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EUserRole getCode() {
        return code;
    }

    public void setCode(EUserRole code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}