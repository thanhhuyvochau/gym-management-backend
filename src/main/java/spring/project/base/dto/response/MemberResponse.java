package spring.project.base.dto.response;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import java.time.Instant;

@Data
public class MemberResponse {
    private long id;
    private String fullName;
    private EGenderType gender;
    private String phoneNumber;
    private Instant birthday;
    private String memberImage;
    private Instant dateEnrolled;
    private Instant dateExpiration;
    private String planName = "No";
}


