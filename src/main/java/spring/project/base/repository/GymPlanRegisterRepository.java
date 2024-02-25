package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.GymPlanRegister;

@Repository
public interface GymPlanRegisterRepository extends JpaRepository<GymPlanRegister, Long> {

}