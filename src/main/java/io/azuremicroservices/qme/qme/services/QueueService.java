package io.azuremicroservices.qme.qme.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.procedure.UnknownSqlResultSetMappingException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.models.ViewQueuePosition;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;

@Service
public class QueueService {

    private final QueuePositionRepository queuePositionRepo;
    private final QueueRepository queueRepo;
    private final CounterRepository counterRepo;

    private final List<SseEmitter> emitters = new ArrayList<>();
    private final AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

    public QueueService(QueuePositionRepository queuePositionRepo,
                        QueueRepository queueRepo, CounterRepository counterRepo) {
        this.queuePositionRepo = queuePositionRepo;
        this.queueRepo = queueRepo;
        this.counterRepo = counterRepo;
    }

    public List<QueuePosition> findActiveQueuePositionsForPrototype(Long queueId) {
        Queue queue = queueRepo.findById(queueId).get();
        return queuePositionRepo.findAllByQueue(queue)
                .stream()
                .filter(x -> x.getState() == QueuePosition.State.ACTIVE_QUEUE ||
                        x.getState() == QueuePosition.State.ACTIVE_REQUEUE)
                .collect(Collectors.toList());
    }

    public List<QueuePosition> findNoShowStatusQueuePositions(Long queueId) {
        Queue queue = queueRepo.findById(queueId).get();
        return queuePositionRepo.findAllByQueue(queue)
                .stream()
                .filter(x -> x.getState() == QueuePosition.State.INACTIVE_NO_SHOW)
                .collect(Collectors.toList());
    }

    public List<QueuePosition> findActiveRejoinStatusQueuePositions(Long queueId) {
        Queue queue = queueRepo.findById(queueId).get();
        return queuePositionRepo.findAllByQueue(queue)
                .stream()
                .filter(x -> x.getState() == QueuePosition.State.ACTIVE_REQUEUE)
                .collect(Collectors.toList());
    }

    public Queue findQueue(Long queueId) {
        return queueRepo.findById(queueId).get();
    }
    

    public void createNewQueue(Vendor vendor, Branch branch,String name, String description,
    							Double timePerClient,Integer notificationPosition, 
    							Double notificationDelay) {
    	queueRepo.save(new Queue(vendor,branch,name,description,
				Queue.State.CLOSED,timePerClient,
				notificationPosition,notificationDelay));
    	}
    
    
    public void editQueue(Long id,String name, String description, Double timePerClient,
    		Integer notificationPosition,Double notificationDelay) {
    	Queue queue= queueRepo.findById(id).get();
    	queue.setName(name);
    	queue.setDescription(description);
    	queue.setTimePerClient(timePerClient);
    	queue.setNotificationPosition(notificationPosition);
    	queue.setNotificationDelay(notificationDelay);
    	queueRepo.save(queue);   	
    }
    
    public void deleteQueue(Long id) {
    	Queue queue= queueRepo.findById(id).get();
    	queueRepo.delete(queue);
    }

    public void addRandomQueueNumber(Long queueId) {
        Queue queue = queueRepo.findById(queueId).get();
        Random rnd = new Random();
        char randomAlphabet = (char) ('A' + rnd.nextInt(26));
        String queueNumber = String.valueOf(randomAlphabet) + rnd.nextInt(10000);
        queuePositionRepo.save(new QueuePosition(
                queue, queueNumber, QueuePosition.State.ACTIVE_QUEUE, LocalDateTime.now()));
    }

    public SseEmitter addEmitter() {
        System.out.println("\n*** Registering client ***");
        SseEmitter emitter = new SseEmitter(-1L);
        System.out.println(emitter);
        emitter.onCompletion(() -> {
            System.out.println("Completed/Timed out: " + emitter.toString());
            emitters.remove(emitter);
        });
        emitters.add(emitter);
        System.out.println("*** Current clients ***");
        emitters.forEach(System.out::println);
        System.out.println();
        return emitter;
    }

    public void refreshBrowsers() {
        emitters.removeIf(Objects::isNull);
        System.out.println("\n*** Sending refresh message to these clients ***");
        emitters.forEach(System.out::println);
        System.out.println();
        emitters.forEach(emitter -> {
            executor.execute(() -> {
                try {
                    emitter.send("refresh");
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }, AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
        });
    }

    public void updateQueueState(Long queueId) {
        Queue q = findQueue(queueId);
        Queue.State currentState = q.getState();
        if(currentState.equals(Queue.State.CLOSED))
            q.setState(Queue.State.OPEN);
        else
            q.setState(Queue.State.CLOSED);
        queueRepo.save(q);
    }

	public List<Queue> findAllQueuesInBranches(List<Branch> branches) {
		return queueRepo.findAllByBranch_IdIn(branches.stream()
				.map(Branch::getId)
				.collect(Collectors.toList()));
	}
	
	public boolean queueNameExistsForBranch(String queueName, Long branchId) {
		return queueRepo.findAllByBranch_IdAndName(branchId, queueName).size() > 0;
	}
	
	public void createQueue(Queue queue) {
		queue.setNotificationPosition(0);
		queueRepo.save(queue);
	}
	
	public Optional<Queue> findQueueById(Long id) {
		return queueRepo.findById(id);
	}
	
	public void updateQueue(Queue queue) {
		queueRepo.save(queue);
	}
	
	public void deleteQueue(Queue queue) {
		queueRepo.delete(queue);
	}

	// Note: add for test; currently used in OperateQueueController
    public List<QueuePosition> findAllQueuePositions(Long queueId) {
        Queue queue = queueRepo.findById(queueId).get();
        return queuePositionRepo.findAllByQueue(queue);
    }

    @Transactional
	public boolean signInCounter(User user, Counter counter) {
		if (counterRepo.findByUser_Id(user.getId()) != null) {
			return false;
		} else {
			counterRepo.findById(counter.getId())
				.ifPresent(c -> c.setUser(user));
		}
		
		return true;
	}
	
    @Transactional
	public boolean signOutCounter(User user, Counter counter) {
		if (counterRepo.findByUser_Id(user.getId()) == null) {
			return false;
		} else {
			counterRepo.findById(counter.getId())
				.ifPresent(c -> c.setUser(null));
		}
		
		return true;
	}

	public Optional<Counter> findCounterById(Long counterId) {
		return counterRepo.findById(counterId);
	}

	public Counter findCounterByUserId(Long id) {
		return counterRepo.findByUser_Id(id);
	}

	@Transactional(readOnly = true)
	public List<ViewQueuePosition> generateViewQueuePositions(Counter counter) {
		List<ViewQueuePosition> viewQueuePositions = new ArrayList<>();
		Queue queue = counter.getQueue();
		State[] activeStates = new State[] { State.ACTIVE_QUEUE, State.ACTIVE_REQUEUE };
		Integer count = 1;
		
		for (QueuePosition qp : queuePositionRepo.findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(queue.getId(), activeStates)) {
			viewQueuePositions.add(new ViewQueuePosition(
				qp, count++, Duration.between(qp.getQueueStartTime(), LocalDateTime.now()).toMinutes()
			));
		}
		
		return viewQueuePositions;
	}	
	
	public Counter findCounterByUser(User user) {
        return counterRepo.findByUser(user);
    }

    public int findQueueLengthByCounter(Counter counter) {
        return (int) counter.getQueue()
                .getQueuePositions()
                .stream()
                .filter(x -> x.getState() == QueuePosition.State.ACTIVE_QUEUE ||
                        x.getState() == QueuePosition.State.ACTIVE_REQUEUE)
                .count();
    }

    @Transactional
    public String callNextNumber(Counter counter) {
        QueuePosition currentlyServing = counter.getCurrentlyServingQueueNumber();

        if (currentlyServing != null) {
            currentlyServing.setState(QueuePosition.State.INACTIVE_COMPLETE);
            currentlyServing.setStateChangeTime(LocalDateTime.now());
            queuePositionRepo.save(currentlyServing);
        }

        if (findQueueLengthByCounter(counter) == 0) {
            counter.setCurrentlyServingQueueNumber(null);
            counterRepo.save(counter);
            return null;
        }

        QueuePosition nextInQueue = counter.getQueue()
                .getQueuePositions()
                .stream()
                .filter(x -> x.getState() == QueuePosition.State.ACTIVE_QUEUE ||
                        x.getState() == QueuePosition.State.ACTIVE_REQUEUE)
                .min(Comparator.comparingInt(QueuePosition::getPosition))
                .get();

        counter.setCurrentlyServingQueueNumber(nextInQueue);
        nextInQueue.setState(QueuePosition.State.ACTIVE_CALLED);
        nextInQueue.setStateChangeTime(LocalDateTime.now());
        nextInQueue.setPosition(null);
        counterRepo.save(counter);
        queuePositionRepo.save(nextInQueue);
        // TODO: Notify client / Update TV screen
        return nextInQueue.getQueueNumber();
    }

    @Transactional
    public String noShow(Counter counter) {
        QueuePosition currentlyServing = queuePositionRepo.findByQueueNumber(counter.getCurrentlyServingQueueNumber().getQueueNumber());

        if (currentlyServing != null) {
            currentlyServing.setState(QueuePosition.State.INACTIVE_NO_SHOW);
            currentlyServing.setStateChangeTime(LocalDateTime.now());
            queuePositionRepo.save(currentlyServing);
            counter.setCurrentlyServingQueueNumber(null);
            counterRepo.save(counter);
            return currentlyServing.getQueueNumber();
        } else {
            return null;
        }
    }
}