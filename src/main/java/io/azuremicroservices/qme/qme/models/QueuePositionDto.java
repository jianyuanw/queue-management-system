package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class QueuePositionDto {
	private QueuePosition queuePosition;
	private Integer position;
	private Long durationInQueue;
	
	public QueuePositionDto(QueuePosition queuePosition, Integer position, Long durationInQueue) {
		this.queuePosition = queuePosition;
		this.position = position;
		this.durationInQueue = durationInQueue;
	}
}
