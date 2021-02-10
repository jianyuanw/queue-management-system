package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;

@Service
public class ClientService {

    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;

    public ClientService(BranchRepository branchRepo, QueueRepository queueRepo) {
        this.branchRepo = branchRepo;
        this.queueRepo = queueRepo;
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
}
