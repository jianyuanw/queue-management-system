package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.UserBranchPermission;

public interface UserBranchPermissionRepository extends JpaRepository<UserBranchPermission, Long>{

}
