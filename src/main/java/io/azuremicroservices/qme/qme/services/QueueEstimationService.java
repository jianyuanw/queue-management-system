package io.azuremicroservices.qme.qme.services;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class QueueEstimationService {

    //1 window = max 10 people queues or less
    public static final int MOVING_AVERAGE_WINDOW = 10;

    private QueuePositionRepository queuePositionRepository;

    @Autowired
    public QueueEstimationService(QueuePositionRepository queuePositionRepository) {
        this.queuePositionRepository = queuePositionRepository;
    }

    /**
     * Estimate queue time in minutes, using moving average.
     *
     * @return estimated queue time in minutes
     */
    public int estimateQueueTime() {
        List<QueuePosition> lastNQueuePosition =
                queuePositionRepository.findLastNCompletedQueuePosition(MOVING_AVERAGE_WINDOW);
        //if nobody queue
        if (lastNQueuePosition.size() == 0) {
            return 0;
        }
        //duration between start and end of each queue
        Duration totalDuration = lastNQueuePosition
                .stream()
                .map(queuePosition ->
                        Duration.between(
                                queuePosition.getQueueStartTime(),
                                queuePosition.getQueueEndTime()))
                .reduce((total, current) -> {
                    return total.plus(current);
                })
//                .reduce(Duration::plus)
                .orElse(Duration.ofMinutes(0));
        //convert to minutes
        int movingAverageInMinutes =
                (int) (totalDuration.toMinutes() / lastNQueuePosition.size());

        return movingAverageInMinutes;
    }
}
