package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.ViewQueue;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;

@Service
public class ClientService {

    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;
    private final QueuePositionRepository queuePositionRepo;
    private final QueueEstimationService queueEstimateService;

    public ClientService(BranchRepository branchRepo, QueueRepository queueRepo, QueuePositionRepository queuePositionRepo, QueueEstimationService queueEstimateService) {
        this.branchRepo = branchRepo;
        this.queueRepo = queueRepo;
        this.queuePositionRepo = queuePositionRepo;
        this.queueEstimateService = queueEstimateService;
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

	public List<Queue> findQueuesByBranchId(String branchId) {
		try {
			return queueRepo.findAllByBranch_Id(Long.parseLong(branchId));
		} catch (NumberFormatException e) {
			return null;
		}
	
	}

	public List<ViewQueue> generateViewQueues(List<Queue> queues) {
		List<ViewQueue> viewQueues = new ArrayList<>();
		State[] activeStates = new State[] { State.ACTIVE_QUEUE, State.ACTIVE_REQUEUE };				
		
		for (Queue queue : queues) {
			Integer inLine = queuePositionRepo.findAllByQueueAndStateIn(queue, activeStates).size();
			Integer waitingTime = queueEstimateService.estimateQueueTime(queue.getId().toString());
			viewQueues.add(new ViewQueue(queue, inLine, waitingTime));
		}

		return viewQueues;
	}

	public Branch findBranchById(String branchId) {
		try {
			return branchRepo.findById(Long.parseLong(branchId)).get();
		} catch (NumberFormatException | NoSuchElementException e) {
			return null;
		}
		
	}
}
