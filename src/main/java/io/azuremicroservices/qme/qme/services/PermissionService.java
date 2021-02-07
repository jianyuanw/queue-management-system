package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
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
		List<Vendor> vendors = userRepo.findById(userId).get().getUserVendorPermissions();
		if (vendors.size() == 0) {
			return null;
		}
		
		return vendors.get(0);
	}
	
	@Transactional
	public List<Branch> getBranchPermissions(Long userId) {
		List<Branch> branchPermissions = userRepo.findById(userId).get().getUserBranchPermissions();
		
		Vendor vendor = this.getVendorPermission(userId);
		
		if (vendor != null) {
			branchPermissions.addAll(branchRepo.findAllByVendor_Id(vendor.getId()));
		}
		
		return branchPermissions;
	}
	
	@Transactional
	public List<Queue> getQueuePermissions(Long userId) {
		List<Queue> queuePermissions = userRepo.findById(userId).get().getUserQueuePermissions();
		
		List<Long> branches = this.getBranchPermissions(userId).stream().map(Branch::getId).collect(Collectors.toList());
		
		if (branches.size() > 0) {
			queuePermissions.addAll(queueRepo.findAllByBranch_IdIn(branches));
		}
		
		return queuePermissions;
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
		boolean authenticated = false;
		if (this.getBranchPermissions(user.getId()).contains(branch)) {
			authenticated = true;
		}
		
		return authenticated;
	}
	
	@Transactional
	public boolean authenticateQueue(User user, Queue queue) {
		boolean authenticated = false;
		if (this.getQueuePermissions(user.getId()).contains(queue)) {
			authenticated = true;
		}
		
		return authenticated;
	}
}
