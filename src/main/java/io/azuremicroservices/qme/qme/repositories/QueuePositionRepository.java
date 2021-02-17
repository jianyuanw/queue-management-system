package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface QueuePositionRepository extends JpaRepository<QueuePosition, Long> {

    /***
     @return: QueuePosition of specific user (@Param userID) on which queue (@Param QueueID) on specific state (@Param state)
     */
    @Query("select qp from QueuePosition as qp where qp.user.id = :userId and qp.queue.id = :queueId and qp.state=:state")
    public List<QueuePosition> findByUserIdQueueIdAndState(@Param("userId") long userId, @Param("queueId") long queueId, @Param("state") QueuePosition.State state);

    /***
     * @
     * @param queueId
     * @param dateTime
     * @return :total number (count) of queue position on which queue on particular day
     */
//    @Query(value = "select count(*) from QueuePosition as qp where qp.queue_id= :queueId and cast(qp.queue_start_time as date)=:qsd", nativeQuery = true)
//    public int findTotalNumberofQueueByQueueIdAndDate(@Param("queueId") long queueId, @Param("qsd") LocalDate dateTime);

    public Integer countByQueue_IdAndQueueStartTimeGreaterThanEqual(Long queueId, LocalDateTime datetime);
    
//    @Query(value = "SELECT * from QueuePosition qp WHERE qp.queue_id = :queueId and qp.state=:state ORDER BY queue_start_time DESC LIMIT :n", nativeQuery = true)
//    public List<QueuePosition> findLastNCompletedQueuePosition(@Param("n") int n, @Param("queueId") long queueId, @Param("state") QueuePosition.State state);

    @Query("SELECT qp FROM QueuePosition qp WHERE qp.queue = :queue")
    public List<QueuePosition> findAllByQueue(@Param("queue") Queue queue);

	public List<QueuePosition> findAllByQueueAndStateIn(Queue queue, List<State> activeStates);

	public List<QueuePosition> findTop10ByQueue_IdAndStateEqualsOrderByQueueStartTimeDesc(Long queueId, State state);

	public List<QueuePosition> findAllByUser_Id(Long userId);

	public Integer countByQueue_IdAndStateIn(Long queueId, List<State> list);

	public List<QueuePosition> findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(Long queueId, List<State> list);

	public QueuePosition findTopByQueue_IdAndStateInOrderByPositionAscPriorityDesc(Long id, State[] activeStates);

	public Integer countByQueue_Id(Long queueId);

	public Integer countByQueueAndStateInAndPositionLessThanEqualAndPriorityGreaterThan(Queue queue,
			List<State> activeStates, QueuePosition queuePosition, Integer priority);

	public List<QueuePosition> findAllByQueueAndStateInAndPositionLessThanEqualAndPriorityGreaterThan(Queue queue,
			List<State> activeStates, QueuePosition queuePosition, Integer priority);

	public List<QueuePosition> findAllByQueue_IdAndStateIn(Long id, List<State> activeStates);

	public QueuePosition findTopByQueue_IdAndQueueStartTimeGreaterThanEqualOrderByPositionDesc(Long queueId, LocalDateTime datetime);

	public List<QueuePosition> findAllByUser_IdAndQueueStartTimeGreaterThanEqualAndStateIn(Long userId, LocalDateTime datetime, List<State> activeStates);

	public List<QueuePosition> findAllByQueue_IdOrderByPositionAscPriorityDesc(Long queueId);

	public List<QueuePosition> findAllByQueue_IdAndState(Long queueId, State inactiveNoShow);

	public List<QueuePosition> findAllByQueue_Branch_IdIn(List<Long> branchIds);
	
	public List<QueuePosition> findAllByQueue_Branch_Id(Long branchId);
	
	@Query(value = "SELECT qp FROM QueuePosition qp JOIN qp.queue q LEFT JOIN FETCH qp.counter c WHERE q.id IN :queueIds")
	public List<QueuePosition> findAllByQueue_IdIn(@Param("queueIds") List<Long> queueIds);

	public List<QueuePosition> findAllByQueueAndStateAndQueueStartTimeAfter(Queue queue, State state, LocalDateTime dateTime);
}
