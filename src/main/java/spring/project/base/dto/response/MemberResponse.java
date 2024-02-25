package spring.project.base.dto.response;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private long id;
    private int age;
    private String fullName;
    private EGenderType gender;
    private String phoneNumber;
    private LocalDateTime birthday;
}


