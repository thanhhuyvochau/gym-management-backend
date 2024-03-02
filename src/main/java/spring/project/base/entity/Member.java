package spring.project.base.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "gym_owner_id")
    private Account gymOwner;
    @Column(name = "encoded_member_data")
    private String encodeMemberData = "";
    @Column(name = "image")
    private String image = "";
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<GymPlanRegister> registerList = new ArrayList<>();
}