package spring.project.base.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import spring.project.base.constant.EGenderType;
import spring.project.base.constant.SocialProvider;

import javax.persistence.*;


@Entity
@Table(name = "account")
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

    private SocialProvider provider = SocialProvider.LOCAL;

    @Column(name = "full_name")
    private String fullName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private EGenderType gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public void setProvider(SocialProvider provider) {
        this.provider = provider;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EGenderType getGender() {
        return gender;
    }

    public void setGender(EGenderType gender) {
        this.gender = gender;
    }
}
