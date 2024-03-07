package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlanRegister;
import spring.project.base.entity.Member;

import java.time.Instant;
import java.util.List;

@Repository
public interface GymPlanRegisterRepository extends JpaRepository<GymPlanRegister, Long> {
    GymPlanRegister findByMemberAndToDateAfter(Member member, Instant now);

    List<GymPlanRegister> findByCreatedIsGreaterThanEqualAndCreatedIsLessThanEqual(Instant fromDate, Instant toDate);


    @Query(value = "SELECT gpr FROM GymPlanRegister gpr WHERE gpr.created >= ?1 AND gpr.created <= ?2 ")
    List<GymPlanRegister> findByCreatedIsGreaterThanEqualAndCreatedIsLessThanEqualAndGymPlan_GymOwner(Instant fromDate, Instant toDate, Long gymOwnerId);

}