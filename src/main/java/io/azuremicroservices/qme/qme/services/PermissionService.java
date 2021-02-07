package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.validator.internal.metadata.aggregated.GetterCascadable.Builder;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class PermissionService {
	private final UserRepository userRepo;
	
	public PermissionService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@Transactional
	public Vendor getVendorPermission(Long userId) {
		return userRepo.findById(userId).get().getUserVendorPermission();
	}
	
	@Transactional
	public List<Branch> getBranchPermissions(Long userId) {
		return userRepo.findById(userId).get().getUserBranchPermissions();
	}
	
	@Transactional
	public List<Queue> getQueuePermissions(Long userId) {
		return userRepo.findById(userId).get().getUserQueuePermissions();
	}	
	
	@Transactional
	public boolean authenticateVendor(User user, Vendor vendor) {
		boolean authenticated = false;
		if (this.getVendorPermission(user.getId()).equals(vendor)) {
			authenticated = true;
		}
		
		return authenticated;
	}
	
	@Transactional
	public boolean authenticateBranch(User user, Branch branch) {
		List<Role> authenticatedRoles = new ArrayList<>(List.of(Role.VENDOR_ADMIN));
		boolean authenticated = false;
		if (user.getUserBranchPermissions().contains(branch)) {
			authenticated = true;
		} else if (authenticatedRoles.contains(user.getRole()) && this.authenticateVendor(user, branch.getVendor())) {
			authenticated = true;
		}
		
		return authenticated;
	}
	
	@Transactional
	public boolean authenticateQueue(User user, Queue queue) {
		List<Role> authenticatedRoles = new ArrayList<>(List.of(Role.VENDOR_ADMIN, Role.BRANCH_ADMIN));
		boolean authenticated = false;
		if (user.getUserQueuePermissions().contains(queue)) {
			authenticated = true;
		} else if (authenticatedRoles.contains(user.getRole()) && this.authenticateBranch(user, queue.getBranch())) {
			authenticated = true;
		}
		
		return authenticated;
	}
}
