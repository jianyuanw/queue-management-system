/*package io.azuremicroservices.qme.qme;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository1;
import io.azuremicroservices.qme.qme.services.QueueEstimationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueEstimationServiceTest {

    QueueEstimationService estimationService;

    @Mock
    QueuePositionRepository1 queuePositionRepository;

    @BeforeEach
    public void setup() {
        estimationService = new QueueEstimationService(queuePositionRepository);
    }

//    @Test
//    public void estimateQueueTime_should_return_movingAvg_of_theLastNQueueDuration() {
//        // given
//        List<QueuePosition> givenLastNQueuePosition = Arrays.asList(
//                queueWithDuration(10, 30, 15),
//                queueWithDuration(10, 30, 30),
//                queueWithDuration(10, 45, 15),
//                queueWithDuration(11, 00, 5),
//                queueWithDuration(11, 00, 10)
//        );
//        when(queuePositionRepository.findLastNCompletedQueuePosition(anyInt()))
//                .thenReturn(givenLastNQueuePosition);
//
//        // when
//        int estimatedQueueTimeInMinutes = estimationService.estimateQueueTime();
//
//        // then
//        int expectedAverageTime = (15 + 30 + 15 + 5 + 10) / 5;
//        // expected compare to the actual
//        assertEquals(expectedAverageTime, estimatedQueueTimeInMinutes);
//    }
//
    @Test
    public void estimateQueueTime_should_handle_situation_when_thereIsNo_previousQueues() {
        // given
        List<QueuePosition> givenLastNQueuePosition = Arrays.asList();

        // when
        int estimatedQueueTimeInMinutes = estimationService.estimateQueueTime();

        // then
        assertEquals(0, estimatedQueueTimeInMinutes);
    }

    private QueuePosition queueWithDuration(
            int startHour, int startMinute,
            int durationInMinutes) {

        QueuePosition queue = new QueuePosition();
        queue.setQueueStartTime(
                LocalDateTime.of(2021, 1, 30, startHour, startMinute, 0)
        );
        queue.setQueueEndTime(
                queue.getQueueStartTime().plusMinutes(durationInMinutes)
        );
        return queue;
    }
}
*/