package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueuePositionRepository extends JpaRepository<QueuePosition, Long> {

    List<QueuePosition> findAllByQueue(Queue queue);
}
