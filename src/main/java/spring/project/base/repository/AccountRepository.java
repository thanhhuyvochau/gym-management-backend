package spring.project.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.project.base.entity.Account;

import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsAccountByPhone(String phone);

}
