package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
