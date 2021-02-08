package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.azuremicroservices.qme.qme.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role = 2")
    List<User> findVendorAdmin();
    @Query("SELECT u FROM User u WHERE u.role = 3")
    List<User> findBranchAdmin();
}
