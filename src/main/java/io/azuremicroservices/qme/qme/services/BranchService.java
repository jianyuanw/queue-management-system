package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Vendor;
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

	public void deleteBranch(Branch branch) {
		branchRepo.delete(branch);
	}	
	
}
