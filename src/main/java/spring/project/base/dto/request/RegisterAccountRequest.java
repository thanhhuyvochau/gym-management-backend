package spring.project.base.dto.request;

import spring.project.base.constant.EGenderType;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public class RegisterAccountRequest {
    @NotNull
    private String fullName;
    @NotNull
    private String email;
    @NotNull
    private String phone;
    @NotNull
    private String password;
    private EGenderType gender;
    @NotNull
    private Instant birthDay;
    @NotNull
    private String address;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EGenderType getGender() {
        return gender;
    }

    public void setGender(EGenderType gender) {
        this.gender = gender;
    }

    public Instant getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Instant birthDay) {
        this.birthDay = birthDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
