package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.Branch;
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

    public List<Branch> findBranchesBySearchTerm(String searchTerm) {
        return branchRepo.findAll()
                .stream()
                .filter(x -> x.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
}
