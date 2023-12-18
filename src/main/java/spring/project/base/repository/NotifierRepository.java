package spring.project.base.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.Account;
import spring.project.base.entity.Notifier;

@Repository
public interface NotifierRepository extends JpaRepository<Notifier, Long> {
    Page<Notifier> findAllByUser(Account account, Pageable pageable);
}
