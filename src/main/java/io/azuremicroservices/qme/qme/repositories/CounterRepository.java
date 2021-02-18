package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.User;

public interface CounterRepository extends JpaRepository<Counter,Long> {
	
	List<Counter> findAllByQueue_IdIn(List<Long> queueIds);

	Counter findByUser_Id(Long id);

	List<Counter> findAllByQueue_Id(Long id);

	List<Counter> findAllByQueue_IdAndUser_IdNull(Long id);

	Counter findByUser(User user);
}
