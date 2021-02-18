package io.azuremicroservices.qme.qme.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchQueueDto;
import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.MyQueueDto;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.QueuePositionDto;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserQueueNumberDto;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class QueueService {

	private final UserRepository userRepo;
    private final QueuePositionRepository queuePositionRepo;
    private final QueueRepository queueRepo;
    private final CounterRepository counterRepo;
    private final NotificationService notificationService;

    private final Map<Long, List<SseEmitter>> queueEmittersMap;
    private final AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

    public QueueService(UserRepository userRepo, QueuePositionRepository queuePositionRepo,
                        QueueRepository queueRepo, CounterRepository counterRepo,
                        NotificationService notificationService) {
    	this.userRepo = userRepo;
        this.queuePositionRepo = queuePositionRepo;
        this.queueRepo = queueRepo;
        this.counterRepo = counterRepo;
        this.notificationService = notificationService;

        queueEmittersMap = queueRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Queue::getId, x -> new ArrayList<>()));
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

    // Uncomment below println for troubleshooting
    public SseEmitter addEmitter(Long queueId) {
//        System.out.println("\n*** Registering client ***");
        SseEmitter emitter = new SseEmitter(-1L);
//        System.out.println(emitter);
        emitter.onCompletion(() -> {
//            System.out.println("Completed/Timed out: " + emitter.toString());
            queueEmittersMap.get(queueId).remove(emitter);
        });
        queueEmittersMap.get(queueId).add(emitter);
//        System.out.println("*** Current clients ***");
//        queueEmittersMap.forEach((k, v) -> {
//            if (v.size() != 0) {
//                System.out.println("Queue " + k);
//                v.forEach(System.out::println);
//            }
//        });
//        System.out.println();
        return emitter;
    }

    // Uncomment below println for troubleshooting
    public void refreshBrowsers(Long queueId) {
        queueEmittersMap.get(queueId).removeIf(Objects::isNull);
//        System.out.println("\n*** Sending refresh message to these clients ***");
//        System.out.println("Queue " + queueId);
//        queueEmittersMap.get(queueId).forEach(System.out::println);
//        System.out.println();
        queueEmittersMap.get(queueId).forEach(emitter -> {
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
    
	public List<Queue> findQueuesByBranchId(Long branchId) {
		try {
			return queueRepo.findAllByBranch_Id(branchId);
		} catch (NumberFormatException e) {
			return null;
		}
	
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
	public List<QueuePositionDto> generateViewQueuePositions(Counter counter) {
		List<QueuePositionDto> viewQueuePositions = new ArrayList<>();
		Queue queue = counter.getQueue();
		Integer count = 1;
		
		for (QueuePosition qp : queuePositionRepo.findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(queue.getId(), QueuePosition.getQueuingStates())) {
			viewQueuePositions.add(new QueuePositionDto(
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

        QueuePosition nextInQueue = queuePositionRepo.findTopByQueue_IdAndStateInOrderByPositionAscPriorityDesc(counter.getQueue().getId(), QueuePosition.getQueuingStates()); 
        		
//        		counter.getQueue()
//                .getQueuePositions()
//                .stream()
//                .filter(x -> x.getState() == QueuePosition.State.ACTIVE_QUEUE ||
//                        x.getState() == QueuePosition.State.ACTIVE_REQUEUE)
//                .min(Comparator.comparingInt(QueuePosition::getPosition).then))
//                .get();

        counter.setCurrentlyServingQueueNumber(nextInQueue);
        nextInQueue.setState(QueuePosition.State.INACTIVE_CALLED);
        nextInQueue.setStateChangeTime(LocalDateTime.now());
        counterRepo.save(counter);
        queuePositionRepo.save(nextInQueue); 
        this.notifyReminderSMS(counter.getQueue().getId());
        return nextInQueue.getQueueNumber();
    }
    
    public List<QueuePosition> findAllOngoingQueuePositions(Long queueId) {    	
    	return queuePositionRepo.findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(queueId, QueuePosition.getQueuingStates());
    }

    @Transactional
    public String noShow(Counter counter) {
        QueuePosition currentlyServing = counter.getCurrentlyServingQueueNumber();

        if (currentlyServing != null) {
            currentlyServing.setState(QueuePosition.State.INACTIVE_NO_SHOW);
            currentlyServing.setStateChangeTime(LocalDateTime.now());
            queuePositionRepo.save(currentlyServing);
            counter.setCurrentlyServingQueueNumber(null);
            counterRepo.save(counter);
            this.notifyMissedSMS(currentlyServing);
            return currentlyServing.getQueueNumber();
        } else {
            return null;
        }
    }
    
    @Transactional
    public boolean notifyReminderSMS(Long queueId) {
    	List<QueuePosition> queuePositions = this.findAllOngoingQueuePositions(queueId);
    	
    	if (queuePositions.size() > 0) {
    		Queue queue = queuePositions.get(0).getQueue();
    		Integer notificationPosition = queue.getNotificationPosition();
    		Double notificationDelay = queue.getNotificationDelay();
    		if (queuePositions.size() >= notificationPosition) {
    			User user = queuePositions.get(notificationPosition - 1).getUser();
    			
    			ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    			
    			Runnable notification = new Runnable() {
    				public void run() {
    					notificationService.addNotification(user.getId(), queue.getName() + " Notification",
    	    					"Dear " + user.getFirstName() + ", \n" + "You are currently at position " + notificationPosition + ", kindly start making your way back to " + 
    	    					queue.getBranch().getAddress() + ".");
    				}
    			};
    			
    			if (notificationDelay == 0) {
    				executorService.execute(notification);
    			} else {
    				executorService.schedule(notification, notificationDelay.longValue(), TimeUnit.MINUTES);
    			}
    			
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public void notifyMissedSMS(QueuePosition queuePosition) {
    	User user = queuePosition.getUser();
    	notificationService.addNotification(queuePosition.getUser().getId(), queuePosition.getQueue().getName() + " Notification", 
				"Dear " + user.getFirstName() + ", \n" + "You have missed your queue number and have dropped out of the queue.");
    	
    }    
    
    
    public boolean enterQueue(String userId, String queueIdStr) {
    	Long queueId = Long.parseLong(queueIdStr);
    	
        Optional<User> user = userRepo.findById(Long.parseLong(userId));        
        Optional<Queue> queue = queueRepo.findById(queueId);                

        if (user.isEmpty() || queue.isEmpty() || queue.get().getState().equals(io.azuremicroservices.qme.qme.models.Queue.State.CLOSED)) {
        	return false;
        }
        
        QueuePosition queuePosition = new QueuePosition();
        Integer queueNumber = obtainQueueNumber(queueId);
        
        // set all attributes including state
        queuePosition.setQueue(queue.get());
        queuePosition.setQueueStartTime(LocalDateTime.now());
        queuePosition.setQueueEndTime(null);
        queuePosition.setUser(user.get());        
        queuePosition.setQueueNumber(queue.get().getName().substring(0, 4) + String.valueOf(queueNumber));
        queuePosition.setPosition(queueNumber);
        queuePosition.setPriority(0);
        queuePosition.setState(QueuePosition.State.ACTIVE_QUEUE);
        queuePositionRepo.save(queuePosition);

        return true;
    }

    // User decide to leave queue without completing their biz
    public UserQueueNumberDto leaveQueue(String userId, String queueIdstr) {
        User user = userRepo
                .findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        long queueId = Long.parseLong(queueIdstr);
        
        Queue queue = queueRepo
                .findById(queueId)
                .orElseThrow(() -> new RuntimeException("Invalid queue: " + queueId));

        queuePositionRepo
                .findByUserIdQueueIdAndState(
                        Long.parseLong(userId),
                        Long.parseLong(queueIdstr),
                        QueuePosition.State.ACTIVE_QUEUE)
                .forEach(queuePosition -> {
                    queuePosition.setState(QueuePosition.State.INACTIVE_LEFT);
                    queuePositionRepo.save(queuePosition);
                });

        return new UserQueueNumberDto(
                user,
                null,
                user.getFirstName() + " " + user.getLastName());
    }


    // This is to restart the Queue Number to 1 every new day
    private Integer obtainQueueNumber(Long queueId) {
        QueuePosition queuePosition = queuePositionRepo.findTopByQueue_IdAndQueueStartTimeGreaterThanEqualOrderByPositionDesc(queueId, LocalDate.now().atStartOfDay());
        
        Integer count;
        
        if (queuePosition == null) {
        	count = 1;
        } else {
        	count = queuePosition.getPosition() + 1;
        }

        return count;
    }
    
	public boolean rejoinQueue(Long queuePositionId) {
	QueuePosition queuePosition = queuePositionRepo.findById(queuePositionId).get();

	if (queuePosition.getQueue().getState() == Queue.State.CLOSED) {
		return false;
	}

	int lastPosition = queuePositionRepo.findAllByQueue_IdAndStateIn(queuePosition.getQueue().getId(), QueuePosition.getQueuingStates()).stream()
			.mapToInt(QueuePosition::getPosition)
			.max()
			.getAsInt();

	queuePosition.setState(State.ACTIVE_REQUEUE);
	queuePosition.setStateChangeTime(LocalDateTime.now());
	queuePosition.setPosition(lastPosition + 1);
	queuePositionRepo.save(queuePosition);

	return true;
}    

    /**
     * Estimate queue time in minutes, using moving average.
     *
     * @return estimated queue time in minutes
     * <p>
     */
    public int estimateQueueTime(String queueId) {
        List<QueuePosition> lastNQueuePosition =
                queuePositionRepo.findTop10ByQueue_IdAndStateEqualsOrderByQueueStartTimeDesc(
                        Long.parseLong(queueId),
                        QueuePosition.State.INACTIVE_COMPLETE
                );
        //if nobody queue
        if (lastNQueuePosition.isEmpty()) {
            return 0;
        }
        //duration between start and end of each queue
        Duration totalDuration = lastNQueuePosition
                .stream()
                .map(queuePosition ->
                        Duration.between(
                                queuePosition.getQueueEndTime(),
                                queuePosition.getQueueStartTime()))
                .reduce((total, current) -> {
                    return total.plus(current);
                })
                .orElse(Duration.ofMinutes(0));
        //convert to minutes
        int movingAverageInMinutes =
                (int) (totalDuration.toMinutes() / lastNQueuePosition.size());

        return movingAverageInMinutes;
    }	
    
	public List<MyQueueDto> generateMyQueueDto(Long userId) {
    	List<MyQueueDto> list = new ArrayList<>();    	
    	
    	List<QueuePosition> currentQueuePositions = queuePositionRepo.findAllByUser_IdAndQueueStartTimeGreaterThanEqualAndStateIn(
    			userId, 
    			LocalDate.now().atStartOfDay(),
    			QueuePosition.getViewedStates());

    	for (QueuePosition queuePosition : currentQueuePositions) {
    		List<QueuePosition> queuePositions = queuePositionRepo.findAllByQueue_IdAndStateIn(queuePosition.getQueue().getId(), QueuePosition.getQueuingStates());
    		int personsInLine = queuePositions.size();

    		int personsInFront = Math.toIntExact(queuePositions.stream()
    				.filter(qp -> qp.getPosition() < queuePosition.getPosition())
    				.count());

    		list.add(new MyQueueDto(
    				queuePosition.getQueue(),
					personsInFront,
					personsInLine,
					this.estimateQueueTime(queuePosition.getQueue().getId().toString()) * personsInFront,
					queuePosition.getQueueNumber(),
					queuePosition.getState(),
					queuePosition.getId().intValue()));
		}

    	return list;
	}
	
	public List<BranchQueueDto> generateBranchQueueDtos(Long userId, List<Queue> queues) {
		List<BranchQueueDto> viewQueues = new ArrayList<>();
		List<State> activeStates = new ArrayList<>();
		activeStates.add(State.ACTIVE_QUEUE);
		activeStates.add(State.ACTIVE_REQUEUE);

		List<QueuePosition> userQueues = queuePositionRepo.findAllByUser_Id(userId);

		for (Queue queue : queues) {
			Integer inLine = queuePositionRepo.findAllByQueueAndStateIn(queue, activeStates).size();
			Integer waitingTime = this.estimateQueueTime(queue.getId().toString());
			boolean userInQueue = userQueues.stream()
					.filter(queuePosition -> activeStates.contains(queuePosition.getState()))
					.map(QueuePosition::getQueue)
					.collect(Collectors.toList())
					.contains(queue);

			viewQueues.add(new BranchQueueDto(queue, inLine, waitingTime, userInQueue));
		}

		return viewQueues;
	}


	public List<Queue> findAllQueuesByBranch_Id(Long branchId) {
		return queueRepo.findAllByBranch_Id(branchId);
	}	
	
	public Long findQueueIdByQueuePositionId(String queuePositionId) {
    	return queuePositionRepo.findById(Long.valueOf(queuePositionId))
				.get()
				.getQueue()
				.getId();
	}
}