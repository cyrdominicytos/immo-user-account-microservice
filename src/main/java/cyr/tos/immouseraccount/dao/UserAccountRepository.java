package cyr.tos.immouseraccount.dao;

import cyr.tos.immouseraccount.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByFirstName(String username);
    Optional<UserAccount> findByLastName(String lastname);
    Optional<UserAccount> findByFirstNameAndLastName(String firstname, String lastname);
    Optional<UserAccount> findByFirstNameOrLastName(String firstname, String lastname);
}
