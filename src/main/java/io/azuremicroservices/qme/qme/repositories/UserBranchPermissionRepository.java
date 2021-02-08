package io.azuremicroservices.qme.qme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserBranchPermission;

import java.util.List;

public interface UserBranchPermissionRepository extends JpaRepository<UserBranchPermission, Long> {
	public UserBranchPermission findByUserAndBranch(User user, Branch branch);

	List<UserBranchPermission> findByUser(User user);
}
