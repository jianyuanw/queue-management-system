package io.azuremicroservices.qme.qme.repositories;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QueuePositionRepository {

    /**
     *
     * @param nPosition the total number of latest queue to query
     * @return N latest completed queue position (that is queue that has
     *              both start time and end time).
     *
     */
    public List<QueuePosition> findLastNCompletedQueuePosition(int nPosition) {
        // TODO (For actual system ): is to add actual JPA query to obtain
        //       latest N queue position

        List<QueuePosition> result = new ArrayList<>(nPosition);
        LocalDateTime dateTime = LocalDateTime.now().minusHours(nPosition);
        for (int i = nPosition; i > 0; i--) {
            LocalDateTime startTime = dateTime;
            long randomMinuteDuration = (long) (Math.random() * 60);

            LocalDateTime endTime = startTime.plusMinutes(randomMinuteDuration);
            result.add(
                    new QueuePosition(
                            startTime,
                            endTime
                    ));

            randomMinuteDuration = (long) (Math.random() * 10);
            dateTime = endTime.plusMinutes(randomMinuteDuration);
        }

        return result;
    }
}
