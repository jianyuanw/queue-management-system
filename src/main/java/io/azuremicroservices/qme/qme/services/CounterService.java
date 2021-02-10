package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;

@Service
public class CounterService {
	
	private final CounterRepository counterRepo;
	
	public CounterService(CounterRepository counterRepo) {
		this.counterRepo = counterRepo;
	}
	
	public List<Counter> findAllCountersInQueues(List<Queue> queues){
		return counterRepo.findAllByQueue_IdIn(queues.stream()
				.map(Queue::getId)
				.collect(Collectors.toList()));
	}
	
	public void createCounter(Counter counter) {
		counterRepo.save(counter);
	}
	
	public Optional<Counter> findCounterById(Long id){
		return counterRepo.findById(id);
	}
	
	public void updateCounter(Counter counter) {
		counterRepo.save(counter);
	}
	
	public void deleteCounter(Counter counter) {
		counterRepo.delete(counter);
	}

}
