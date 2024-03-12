package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.base.entity.Account;
import spring.project.base.entity.GymPlan;

import java.util.List;
import java.util.Optional;

public interface GymPlanRepository extends JpaRepository<GymPlan, Long> {
    List<GymPlan> findAllByGymOwner(Account gymOwner);
    Optional<GymPlan> findByIdAndGymOwner(Long id, Account gymOwner);
}