package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.UserVendorPermission;

public interface UserVendorPermissionRepository extends JpaRepository<UserVendorPermission, Long>{
	
}
