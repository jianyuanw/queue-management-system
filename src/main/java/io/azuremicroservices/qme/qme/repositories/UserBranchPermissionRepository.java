package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.UserBranchPermission;

public interface UserBranchPermissionRepository extends JpaRepository<UserBranchPermission, Long>{

	List<UserBranchPermission> findAllByBranchIdIn(List<Long> branches);

}
