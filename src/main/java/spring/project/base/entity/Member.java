package spring.project.base.entity;

import lombok.Data;
import spring.project.base.constant.EGenderType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "age")
    private int age;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "gender")
    private EGenderType gender;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "birthday")
    private LocalDateTime birthday;
    @ManyToOne
    @JoinColumn(name = "gym_owner_id")
    private Account gymOwner;
}