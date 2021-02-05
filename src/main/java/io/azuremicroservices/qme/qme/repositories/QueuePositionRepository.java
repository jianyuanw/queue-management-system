package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface QueuePositionRepository extends JpaRepository<QueuePosition,Long> {

    /***
    @return: QueuePosition of specific user (@Param userID) on which queue (@Param QueueID) on specific state (@Param state)
     */
    @Query("select qp from QueuePosition as qp where qp.user.id = :userId and qp.queue.id = :queueId and qp.state=:state ")
    public List<QueuePosition> findByUserIdQueueIdAndState(@Param("userId") long userId, @Param("queueId")long queueId, @Param("state") QueuePosition.State state);

    /***
     * @
     * @param queueId
     * @param dateTime
     * @return :total number (count) of queue position on which queue on particular day
     */
    @Query(value = "select count(*) from Queue_Position as qp where qp.queue_id= :queueId and cast(qp.queue_start_time as date)=:qsd", nativeQuery = true)
    public int findTotalNumberofQueueByQueueIdAndDate(@Param("queueId")long queueId, @Param("qsd") LocalDate dateTime);
}
