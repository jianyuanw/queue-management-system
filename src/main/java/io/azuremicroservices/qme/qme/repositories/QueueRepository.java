package io.azuremicroservices.qme.qme.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long> {

	List<Queue> findAllByBranch_Id(Long id);

	List<Queue> findAllByBranch_IdIn(List<Long> branchIds);
}
