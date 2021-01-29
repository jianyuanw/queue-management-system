package io.azuremicroservices.qme.qme.repos;

import io.azuremicroservices.qme.qme.models.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue, Long> {
}
