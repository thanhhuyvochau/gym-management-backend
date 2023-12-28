package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.base.entity.GymPlan;

public interface GymPlanRepository extends JpaRepository<GymPlan, Long> {
}