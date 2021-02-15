package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.azuremicroservices.qme.qme.models.Queue;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

	List<Queue> findAllByBranch_Id(Long id);

	List<Queue> findAllByBranch_IdIn(List<Long> branchIds);
	
	List<Queue> findAllByBranch_IdAndName(Long branchId, String queueName);

}
