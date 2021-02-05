package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserVendorPermission;
import io.azuremicroservices.qme.qme.models.Vendor;

public interface UserVendorPermissionRepository extends JpaRepository<UserVendorPermission, Long> {
	public UserVendorPermission findByUserAndVendor(User user, Vendor vendor);
}
