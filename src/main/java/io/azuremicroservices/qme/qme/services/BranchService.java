package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
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

	public Branch createBranch(Branch branch) {
		return branchRepo.save(branch);
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
		branchRepo.delete(branch);
	}	
	
    public List<Branch> findBranchesByQuery(String query) {
        return branchRepo.findAllByNameContaining(query);
    }

	public List<Branch> findBranchesByCategory(String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategory(branchCategory);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public List<Branch> findBranchesByQueryAndCategory(String query, String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategoryAndNameContaining(branchCategory, query);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public String parseSearchQuery(String query, String category, List<Branch> branches) {
		String messageQuery = "";		
		
		if (category == null) {
			branches.addAll(this.findBranchesByQuery(query));
			messageQuery = "Search: " + query;
		} else if (query == null || query == "") {
			branches.addAll(this.findBranchesByCategory(category));
			messageQuery = "Category: " + category;
		} else {
			branches.addAll(this.findBranchesByQueryAndCategory(query, category));
			messageQuery = "Category: " + category + ", Search: " + query;
		}		
		
		return messageQuery;
	}

	
}
