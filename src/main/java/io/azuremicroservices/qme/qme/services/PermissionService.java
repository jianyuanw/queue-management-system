package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserBranchPermission;
import io.azuremicroservices.qme.qme.models.UserQueuePermission;
import io.azuremicroservices.qme.qme.models.UserVendorPermission;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class PermissionService {
	private final UserRepository userRepo;	
	private final BranchRepository branchRepo;
	private final QueueRepository queueRepo;
	
	public PermissionService(UserRepository userRepo, BranchRepository branchRepo, QueueRepository queueRepo) {
		this.userRepo = userRepo;		
		this.branchRepo = branchRepo;
		this.queueRepo = queueRepo;
		
	}
	
	@Transactional
	public Vendor getVendorPermission(Long userId) {		
		List<UserVendorPermission> vendors = userRepo.findById(userId).get().getUserVendorPermissions();
		if (vendors.size() > 1) {
			throw new RuntimeException("Has more than one user vendor permission which is not supposed to happen");
		} else if (vendors.size() == 0) {
			return null;
		}
		
		return vendors.get(0).getVendor();
	}
	
	@Transactional(readOnly = true)
	public List<Branch> getBranchPermissions(Long userId) {
		List<UserBranchPermission> branchPermissions = userRepo.findById(userId).get().getUserBranchPermissions();
		
		List<Branch> localBranchPermissions = branchPermissions.stream().map(ubp -> ubp.getBranch()).collect(Collectors.toList());
		
		Vendor vendor = this.getVendorPermission(userId);
		
		if (vendor != null) {
			localBranchPermissions.addAll(branchRepo.findAllByVendor_Id(vendor.getId()));
		}
	
		return localBranchPermissions;
	}
	
	@Transactional(readOnly = true)
	public List<Queue> getQueuePermissions(Long userId) {
		List<UserQueuePermission> queuePermissions = userRepo.findById(userId).get().getUserQueuePermissions();
		
		List<Queue> localQueuePermissions = queuePermissions.stream().map(uqp -> uqp.getQueue()).collect(Collectors.toList());
		
		List<Long> branches = this.getBranchPermissions(userId).stream().map(Branch::getId).collect(Collectors.toList());
		
		if (branches.size() > 0) {
			localQueuePermissions.addAll(queueRepo.findAllByBranch_IdIn(branches));
		}
		
		return localQueuePermissions;
	}	
	
	@Transactional(readOnly = true)
	public boolean authenticateVendor(User user, Vendor vendor) {
		boolean authenticated = false;
		if (this.getVendorPermission(user.getId()).equals(vendor)) {
			authenticated = true;
		}
		
		return authenticated;
	}
	
	@Transactional(readOnly = true)
	public boolean authenticateBranch(User user, Branch branch) {
		boolean authenticated = false;
		if (this.getBranchPermissions(user.getId()).contains(branch)) {
			authenticated = true;
		}
		
		return authenticated;
	}
	
	@Transactional(readOnly = true)
	public boolean authenticateQueue(User user, Queue queue) {
		boolean authenticated = false;
		if (this.getQueuePermissions(user.getId()).contains(queue)) {
			authenticated = true;
		}
		
		return authenticated;
	}
}
