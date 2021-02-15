package io.azuremicroservices.qme.qme.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.MyQueueDto;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.BranchQueueDto;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;

@Service
public class QueuePositionService {
    private final QueuePositionRepository queuePositionRepo;
    private final CounterRepository counterRepo;

    public QueuePositionService(QueuePositionRepository queuePositionRepo, CounterRepository counterRepo) {
        this.queuePositionRepo = queuePositionRepo;
        this.counterRepo = counterRepo;
    }

    public void updateReassignedIdPriority(Long reassignedId) {
        QueuePosition qp = queuePositionRepo.findById(reassignedId).get();
        Queue q = qp.getQueue();

        List<QueuePosition> qps = q.getQueuePositions();
        int p = 0;
        for (int i = 0; i<qps.size(); i++) {
            if(p < qps.get(i).getPriority())
                p = qps.get(i).getPriority();
        }
        qp.setPriority(p+1);
        queuePositionRepo.save(qp);
    }

    public void setDefaultPriorityFor(Long queuePositionId) {
        QueuePosition qp = queuePositionRepo.findById(queuePositionId).get();
        qp.setPriority(0);
        queuePositionRepo.save(qp);
    }

    public List<QueuePosition> getAllSortedQueuePositions(Long queueId) {
    	return queuePositionRepo.findAllByQueue_IdOrderByPositionAscPriorityDesc(queueId);
    }

    public List<QueuePosition> getActiveSortedQueuePositions(Long queueId) {
    	return queuePositionRepo.findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(queueId, QueuePosition.getQueuingStates());
    }

	public HashMap<Queue, Integer> findAllQueuePositionsInQueues(List<Queue> queuePermissions) {
		HashMap<Queue, Integer> queueCount = new HashMap<>();

		for (Queue queue : queuePermissions) {
			queueCount.put(queue, queuePositionRepo.countByQueue_IdAndStateIn(queue.getId(), QueuePosition.getQueuingStates()));
		}
		
		return queueCount;
	}

	public Counter findCounterByUserId(Long id) {
		return counterRepo.findByUser_Id(id);		
	}

	public Map<Long, List<Counter>> findAllCountersByQueueId(List<Queue> queues) {
		Map<Long, List<Counter>> queueCounters = new HashMap<>();
		
		for (Queue queue : queues) {
			queueCounters.put(queue.getId(), counterRepo.findAllByQueue_IdAndUser_IdNull(queue.getId()));
		}
		
		return queueCounters;
	}



	public List<QueuePosition> findAllQueuePositionsByQueueIdAndActiveStates(Long queueId) {
		return queuePositionRepo.findAllByQueue_IdAndStateIn(queueId, QueuePosition.getQueuingStates());
	}

	public List<QueuePosition> findAllQueuePositionsByQueueIdAndState(Long queueId, State inactiveNoShow) {
		return queuePositionRepo.findAllByQueue_IdAndState(queueId, inactiveNoShow);
	}    

}
