package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;

    public ClientService(BranchRepository branchRepo, QueueRepository queueRepo) {
        this.branchRepo = branchRepo;
        this.queueRepo = queueRepo;
    }

    public List<Branch> findBranchesByQuery(String query) {
        return branchRepo.findAll()
                .stream()
                .filter(x -> x.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Branch findBranchById(Long id) {
        return branchRepo.findById(id).get();
    }

    public List<Queue> findQueuesByBranchId(Long branchId) {
        return branchRepo.findById(branchId).get().getQueues();
    }
}
