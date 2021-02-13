package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueuePositionServiceTest {

    @Mock
    QueuePositionRepository queuePositionRepository;
    @Mock
    QueueRepository queueRepository;
    @Mock
    QueueService queueService;
    @Mock
    CounterRepository counterRepository;

    QueuePositionService service;

    @BeforeEach
    public void setup() {
        service = new QueuePositionService(
                queuePositionRepository,
                queueRepository,
                queueService,
                counterRepository
        );
    }

    @Test
    public void user_is_blacklisted_when_the_last_N_consecutive_queue_are_noshows() {
        // given
        long givenArbitraryUserId = 5;

        List<QueuePosition> givenLastNUserQueuePosition =
                Arrays.asList(
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 13, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 12, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 11, 14, 0))
                );

        when(queuePositionRepository.findAllByUser_Id(
                        eq(givenArbitraryUserId))).thenReturn(givenLastNUserQueuePosition);

        // when
        boolean userIsBlackListed = service.isUserBlacklisted(givenArbitraryUserId);

        // then
        assertTrue(userIsBlackListed);
    }

    private QueuePosition queuePosition(
            QueuePosition.State state, LocalDateTime startTime) {

        QueuePosition qp = new QueuePosition();
        qp.setState(state);
        qp.setQueueStartTime(startTime);
        return qp;
    }


    @Test
    public void user_is_not_yet_blacklisted_if_last_N_consecutive_queue_are_NOT_all_noshows_scenario1() {
        // given
        long givenArbitraryUserId = 5;

        List<QueuePosition> givenLastNUserQueuePosition =
                Arrays.asList(
                        queuePosition(QueuePosition.State.INACTIVE_COMPLETE, LocalDateTime.of(2021, 2, 13, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 12, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 11, 14, 0))
                );

        when(queuePositionRepository.findAllByUser_Id(
                eq(givenArbitraryUserId))).thenReturn(givenLastNUserQueuePosition);

        // when
        boolean userIsBlackListed = service.isUserBlacklisted(givenArbitraryUserId);

        // then
        assertFalse(userIsBlackListed);
    }

    @Test
    public void user_is_not_yet_blacklisted_if_last_N_consecutive_queue_are_NOT_all_noshows_scenario2() {
        // given
        long givenArbitraryUserId = 5;

        List<QueuePosition> givenLastNUserQueuePosition =
                Arrays.asList(
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 13, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_COMPLETE, LocalDateTime.of(2021, 2, 12, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 11, 14, 0))
                );

        when(queuePositionRepository.findAllByUser_Id(
                eq(givenArbitraryUserId))).thenReturn(givenLastNUserQueuePosition);

        // when
        boolean userIsBlackListed = service.isUserBlacklisted(givenArbitraryUserId);

        // then
        assertFalse(userIsBlackListed);
    }

    @Test
    public void user_is_not_yet_blacklisted_if_last_N_consecutive_queue_are_NOT_all_noshows_scenario3() {
        // given
        long givenArbitraryUserId = 5;

        List<QueuePosition> givenLastNUserQueuePosition =
                Arrays.asList(
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 13, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_NO_SHOW, LocalDateTime.of(2021, 2, 12, 14, 0)),
                        queuePosition(QueuePosition.State.INACTIVE_COMPLETE, LocalDateTime.of(2021, 2, 11, 14, 0))
                );

        when(queuePositionRepository.findAllByUser_Id(
                eq(givenArbitraryUserId))).thenReturn(givenLastNUserQueuePosition);

        // when
        boolean userIsBlackListed = service.isUserBlacklisted(givenArbitraryUserId);

        // then
        assertFalse(userIsBlackListed);
    }

}