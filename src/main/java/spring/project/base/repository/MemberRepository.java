package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}