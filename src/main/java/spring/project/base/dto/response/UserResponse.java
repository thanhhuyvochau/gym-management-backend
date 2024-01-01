package spring.project.base.dto.response;

import spring.project.base.constant.EGenderType;

import java.time.Instant;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Instant birthday;
    private String address;
    private String phone;
    private Boolean status;
    private EGenderType gender;
    private RoleResponse roleResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getBirthday() {
        return birthday;
    }

    public void setBirthday(Instant birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public EGenderType getGender() {
        return gender;
    }

    public void setGender(EGenderType gender) {
        this.gender = gender;
    }

    public RoleResponse getRoleDto() {
        return roleResponse;
    }

    public void setRoleDto(RoleResponse roleResponse) {
        this.roleResponse = roleResponse;
    }
}
