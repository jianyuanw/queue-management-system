package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserQueuePermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserQueuePermissionRepository extends JpaRepository<UserQueuePermission, Long>{
	public UserQueuePermission findByUserAndQueue(User user, Queue queue);

	@Query("select uqp from UserQueuePermission uqp where uqp.user.id=:userid")
	public List<UserQueuePermission> findByUser(@Param("userid")Long userId);
}
