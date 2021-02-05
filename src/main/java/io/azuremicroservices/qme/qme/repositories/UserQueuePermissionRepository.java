package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserQueuePermission;

public interface UserQueuePermissionRepository extends JpaRepository<UserQueuePermission, Long>{
	public UserQueuePermission findByUserAndQueue(User user, Queue queue);
}
