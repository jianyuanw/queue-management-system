package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class MyQueueDto {
    private Queue queue;
    private String branchAddress;
    private int personsInFront;
    private int waitTime;
    private String queueNumber;
    private QueuePosition.State state;
    private int queuePositionId;

    public MyQueueDto(Queue queue, int personsInFront, int waitTime, String queueNumber, QueuePosition.State state, int queuePositionId) {
        this.queue = queue;        
        this.personsInFront = personsInFront;
        this.waitTime = waitTime;
        this.queueNumber = queueNumber;
        this.state = state;
        this.queuePositionId = queuePositionId;
    }

}
