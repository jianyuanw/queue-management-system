package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final QueueRepository queueRepo;

    public ClientService(QueueRepository queueRepo) {
        this.queueRepo = queueRepo;
    }


}
