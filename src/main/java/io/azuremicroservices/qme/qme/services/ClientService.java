package io.azuremicroservices.qme.qme.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.azuremicroservices.qme.qme.models.*;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;

@Service
public class ClientService {

    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;
    private final QueuePositionRepository queuePositionRepo;
    private final QueueEstimationService queueEstimateService;
    private final UserRepository userRepo;

    public ClientService(BranchRepository branchRepo,
						 QueueRepository queueRepo,
						 QueuePositionRepository queuePositionRepo,
						 QueueEstimationService queueEstimateService,
						 UserRepository userRepo) {
        this.branchRepo = branchRepo;
        this.queueRepo = queueRepo;
        this.queuePositionRepo = queuePositionRepo;
        this.queueEstimateService = queueEstimateService;
        this.userRepo = userRepo;
    }

    public List<Branch> findBranchesByQuery(String query) {
        return branchRepo.findAllByNameContaining(query);
    }

	public List<Branch> findBranchesByCategory(String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategory(branchCategory);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public List<Branch> findBranchesByQueryAndCategory(String query, String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategoryAndNameContaining(branchCategory, query);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public List<Queue> findQueuesByBranchId(String branchId) {
		try {
			return queueRepo.findAllByBranch_Id(Long.parseLong(branchId));
		} catch (NumberFormatException e) {
			return null;
		}
	
	}

	public List<ViewQueue> generateViewQueues(Long userId, List<Queue> queues) {
		List<ViewQueue> viewQueues = new ArrayList<>();
		List<State> activeStates = new ArrayList<>();
		activeStates.add(State.ACTIVE_QUEUE);
		activeStates.add(State.ACTIVE_REQUEUE);
					
		List<QueuePosition> userQueues = queuePositionRepo.findAllByUser_Id(userId);

		for (Queue queue : queues) {
			Integer inLine = queuePositionRepo.findAllByQueueAndStateIn(queue, activeStates).size();
			Integer waitingTime = queueEstimateService.estimateQueueTime(queue.getId().toString());
			boolean userInQueue = userQueues.stream()
					.filter(queuePosition -> activeStates.contains(queuePosition.getState()))
					.map(QueuePosition::getQueue)					
					.collect(Collectors.toList())					
					.contains(queue);

			viewQueues.add(new ViewQueue(queue, inLine, waitingTime, userInQueue));
		}

		return viewQueues;
	}

	public Branch findBranchById(String branchId) {
		try {
			return branchRepo.findById(Long.parseLong(branchId)).get();
		} catch (NumberFormatException | NoSuchElementException e) {
			return null;
		}
		
	}

    public Branch findBranchById(Long id) {
        return branchRepo.findById(id).get();
    }

    public List<Queue> findQueuesByBranchId(Long branchId) {
        return branchRepo.findById(branchId).get().getQueues();
    }

	public List<MyQueueDto> generateMyQueueDto(Long userId) {
    	List<MyQueueDto> list = new ArrayList<>();    	
    	List<State> activeStates = new ArrayList<>();
    	activeStates.add(State.ACTIVE_QUEUE);
    	activeStates.add(State.ACTIVE_REQUEUE);
    	
    	List<QueuePosition> currentQueuePositions = userRepo.findById(userId)
				.get()
				.getQueuePositions()
				.stream()
				.filter(x -> x.getQueueStartTime().getYear() == LocalDateTime.now().getYear() &&
						x.getQueueStartTime().getDayOfYear() == LocalDateTime.now().getDayOfYear())
				.filter(x -> x.getState() == State.ACTIVE_QUEUE ||
						x.getState() == State.ACTIVE_REQUEUE ||
						x.getState() == State.INACTIVE_NO_SHOW)
				.collect(Collectors.toList());

    	for (QueuePosition queuePosition : currentQueuePositions) {
    		int personsInLine = queuePositionRepo.findAllByQueueAndStateIn(queuePosition.getQueue(), activeStates).size();
    		// int personsInLine = queuePositionRepo.countByQueue_IdAndStateIn(queuePosition.getQueue().getId(), new State[] { State.ACTIVE_QUEUE, State.ACTIVE_REQUEUE});
    		int personsInFront = queuePositionRepo.countByQueueAndStateInAndPositionLessThanEqualAndPriorityGreaterThan(queuePosition.getQueue(), activeStates, queuePosition, queuePosition.getPriority());
    		list.add(new MyQueueDto(
    				queuePosition.getQueue(),
					personsInFront,
					personsInLine,
					queueEstimateService.estimateQueueTime(queuePosition.getQueue().getId().toString()) * personsInFront,
					queuePosition.getQueueNumber(),
					queuePosition.getState(),
					queuePosition.getId().intValue()));
		}

    	return list;
	}

	public boolean rejoinQueue(Long queuePositionId) {
    	QueuePosition queuePosition = queuePositionRepo.findById(queuePositionId).get();

    	if (queuePosition.getQueue().getState() == Queue.State.CLOSED) {
    		return false;
		}

    	int lastPosition = queuePosition.getQueue()
				.getQueuePositions()
				.stream()
				.filter(x -> x.getState() == State.ACTIVE_QUEUE ||
						x.getState() == State.ACTIVE_REQUEUE)
				.mapToInt(QueuePosition::getPosition)
				.max()
				.getAsInt();

    	queuePosition.setState(State.ACTIVE_REQUEUE);
    	queuePosition.setStateChangeTime(LocalDateTime.now());
    	queuePosition.setPosition(lastPosition + 1);
    	queuePositionRepo.save(queuePosition);

    	return true;
	}
}
