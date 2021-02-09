package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

	List<User> findAllByRole(Role role);

	User findByRole(Role role);

	@Modifying
	@Query("update User u set u.perspective = :target where u.id = :userid")
	void updateUserPerspective(@Param("userid") Long userId, @Param("target") Role role);

	User findByUsernameAndIdNot(String username, Long id);
	User findByEmailAndIdNot(String email, Long id);
	
}
