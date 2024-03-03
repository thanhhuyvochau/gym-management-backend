package spring.project.base.dto.other;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import java.time.Instant;

@Data
public class MemberData {
    private String fullName;
    private EGenderType gender;
    private String phoneNumber;
    private Instant birthday;
    private String memberImage;
}
