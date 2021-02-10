package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class ViewQueue {
	private Queue queue;
	private Integer inLine;
	private Integer waitingTime;
	
	public ViewQueue(Queue queue, Integer inline, Integer waitingTime) {
		this.queue = queue;
		this.inLine = inline;
		this.waitingTime = waitingTime;
	}
}
