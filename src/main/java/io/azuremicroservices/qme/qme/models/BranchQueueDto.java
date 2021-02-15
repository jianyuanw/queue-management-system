package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class BranchQueueDto {
	private Queue queue;
	private Integer inLine;
	private Integer waitingTime;
	private boolean userInQueue;
	
	public BranchQueueDto(Queue queue, Integer inline, Integer waitingTime, boolean userInQueue) {
		this.queue = queue;
		this.inLine = inline;
		this.waitingTime = waitingTime;
		this.userInQueue = userInQueue;
	}

}
