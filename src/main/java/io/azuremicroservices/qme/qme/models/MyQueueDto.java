package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class MyQueueDto {
    private String queueName;
    private String branchAddress;
    private int personsInFront;
    private int waitTime;
    private String queueNumber;
    private QueuePosition.State state;
    private int queuePositionId;

    public MyQueueDto(String queueName, String branchAddress, int personsInFront, int waitTime, String queueNumber, QueuePosition.State state, int queuePositionId) {
        this.queueName = queueName;
        this.branchAddress = branchAddress;
        this.personsInFront = personsInFront;
        this.waitTime = waitTime;
        this.queueNumber = queueNumber;
        this.state = state;
        this.queuePositionId = queuePositionId;
    }
}
