package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.UserQueuePermission;

public interface UserQueuePermissionRepository extends JpaRepository<UserQueuePermission, Long>{

	List<UserQueuePermission> findAllByQueueIdIn(List<Long> queues);

}
