package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.GymPlanRegister;
import spring.project.base.entity.Member;

import java.time.Instant;
import java.util.List;

@Repository
public interface GymPlanRegisterRepository extends JpaRepository<GymPlanRegister, Long> {
    GymPlanRegister findByMemberAndToDateAfter(Member member, Instant now);
}