package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserQueueNumberDto;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ManageUserQueueService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueuePositionRepository queuePosRepo;

    @Autowired
    private UserRepository userRepo;

    //private Map<Long, AtomicInteger> queueNumberMap = new HashMap<>();

    public UserQueueNumberDto enterQueue(String userId, String queueIdStr) {
        User user = userRepo
                .findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        long queueId = Long.parseLong(queueIdStr);
        Queue queue = queueRepository
                .findById(queueId)
                .orElseThrow(() -> new RuntimeException("Invalid queue: " + queueId));

        QueuePosition queuePosition = new QueuePosition();

        // set all attributes including state
        queuePosition.setQueue(queue);
        queuePosition.setQueueStartTime(LocalDateTime.now());
        queuePosition.setQueueEndTime(null);
        queuePosition.setUser(user);
        queuePosition.setQueueNumber(obtainQueueNumber(queueId));
        queuePosition.setState(QueuePosition.State.ACTIVE_QUEUE);
        queuePosRepo.save(queuePosition);

        return new UserQueueNumberDto(
                user,
                queuePosition.getQueueNumber(),
                user.getFirstName() + " " + user.getLastName());
    }

    // User decide to leave queue without completing their biz
    public UserQueueNumberDto leaveQueue(String userId, String queueIdstr) {
        User user = userRepo
                .findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        long queueId = Long.parseLong(queueIdstr);
        Queue queue = queueRepository
                .findById(queueId)
                .orElseThrow(() -> new RuntimeException("Invalid queue: " + queueId));

        queuePosRepo
                .findByUserIdQueueIdAndState(
                        Long.parseLong(userId),
                        Long.parseLong(queueIdstr),
                        QueuePosition.State.ACTIVE_QUEUE)
                .forEach(queuePosition -> {
                    queuePosition.setState(QueuePosition.State.INACTIVE_LEFT);
                    queuePosRepo.save(queuePosition);
                });

        return new UserQueueNumberDto(
                user,
                null,
                user.getFirstName() + " " + user.getLastName());
    }


//    private String obtainQueueNumber(Long queueId) {
//        AtomicInteger branchQueueNumberCounter = queueNumberMap.get(queueId);
//        if (branchQueueNumberCounter == null) {
//            branchQueueNumberCounter = new AtomicInteger(0);
//            queueNumberMap.put(queueId, branchQueueNumberCounter);
//        }
//        return branchQueueNumberCounter.incrementAndGet() + "";
//    }
//

    // This is to restart the Queue Number to 1 every new day
    private String obtainQueueNumber(Long queueId) {
        int count = queuePosRepo
                .findTotalNumberofQueueByQueueIdAndDate(
                        Long.parseLong(String.valueOf(queueId)),
                        LocalDate.now());

        count = count + 1;
        return String.valueOf(count);
    }


}


