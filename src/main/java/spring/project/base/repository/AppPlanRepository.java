package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import spring.project.base.entity.AppPlan;

public interface AppPlanRepository extends JpaRepository<AppPlan, Long>, JpaSpecificationExecutor<AppPlan> {
}