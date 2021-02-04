package io.azuremicroservices.qme.qme.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.repositories.BranchRepository;

@Service
public class BranchService {
	
	private final BranchRepository branchRepo;
	
	@Autowired
	public BranchService(BranchRepository branchRepo) {
		this.branchRepo = branchRepo;
	}
	
	
}
