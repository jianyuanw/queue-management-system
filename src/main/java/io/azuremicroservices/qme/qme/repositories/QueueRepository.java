package io.azuremicroservices.qme.qme.repositories;

import java.util.Collection;
import java.util.List;

import io.azuremicroservices.qme.qme.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.azuremicroservices.qme.qme.models.Queue;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

	List<Queue> findAllByBranch_Id(Long id);

	List<Queue> findAllByBranch_IdIn(List<Long> branchIds);

}
