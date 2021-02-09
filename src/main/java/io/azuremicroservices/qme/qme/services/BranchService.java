package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;

import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;

@Service
public class BranchService {
	
	private final BranchRepository branchRepo;
	
	@Autowired
	public BranchService(BranchRepository branchRepo) {
		this.branchRepo = branchRepo;
	}

	public List<Branch> findAllBranches() {
		return branchRepo.findAll();
	}

	public void createBranch(Branch branch) {
		branchRepo.save(branch);
	}
	
	public void updateBranch(Branch branch) {
		branchRepo.save(branch);
	}	

	public Optional<Branch> findBranchById(Long branchId) {
		return branchRepo.findById(branchId);
	}

	public List<Branch> findAllBranchesByVendorId(Long vendorId) {		
		return branchRepo.findAllByVendor_Id(vendorId);
	}
	
	public boolean branchNameExistsForVendor(String branchName, Long vendorId) {
		return branchRepo.findAllByVendor_IdAndName(vendorId, branchName).size() > 0;
	}

	@Transactional
	public void deleteBranch(Branch branch) {
		var dbBranch = this.findBranchById(branch.getId());
		
		if (dbBranch.isPresent()) {
			var dbBranchNew = dbBranch.get(); 
			dbBranchNew.getUserBranchPermissions().clear();	
			for (Queue queue : dbBranchNew.getQueues()) {
				queue.getUserQueuePermissions().clear();
			}
			dbBranchNew.getQueues().clear();
			branchRepo.delete(dbBranchNew);
		}
	}	
	
}
