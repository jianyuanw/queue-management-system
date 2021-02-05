package io.azuremicroservices.qme.qme.services;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.UserBranchPermissionRepository;
import io.azuremicroservices.qme.qme.repositories.UserQueuePermissionRepository;
import io.azuremicroservices.qme.qme.repositories.UserVendorPermissionRepository;

@Service
public class PermissionService {
	private final UserVendorPermissionRepository userVendorPermissionRepo;
	private final UserBranchPermissionRepository userBranchPermissionRepo;
	private final UserQueuePermissionRepository userQueuePermissionRepo;
	
	public PermissionService(UserVendorPermissionRepository userVendorPermissionRepo, 
			UserBranchPermissionRepository userBranchPermissionRepo, UserQueuePermissionRepository userQueuePermissionRepo) {
		this.userVendorPermissionRepo = userVendorPermissionRepo;
		this.userBranchPermissionRepo = userBranchPermissionRepo;
		this.userQueuePermissionRepo = userQueuePermissionRepo;
	}
	
	public boolean authenticateVendor(User user, Vendor vendor) {
		boolean authenticated = false;
		if (userVendorPermissionRepo.findByUserAndVendor(user, vendor) != null) {
			authenticated = true;
		}
		return authenticated;
	}
	
	public boolean authenticateBranch(User user, Branch branch) {
		boolean authenticated = false;
		if (userBranchPermissionRepo.findByUserAndBranch(user, branch) != null) {
			authenticated = true;
		}
		return authenticated;
	}
	
	public boolean authenticateQueue(User user, Queue queue) {
		boolean authenticated = false;
		if (userQueuePermissionRepo.findByUserAndQueue(user, queue) != null) {
			authenticated = true;
		}
		return authenticated;
	}	
}
