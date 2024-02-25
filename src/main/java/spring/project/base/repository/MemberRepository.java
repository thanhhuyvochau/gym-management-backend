package spring.project.base.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Page<Member> findAllByGymOwner_Id(Long gymOwnerId, Pageable pageable);
}