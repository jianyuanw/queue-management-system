package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class ScorecardDto {
	private Long maxWaitingTime;
	private Long customerCount;
	private Long noShowCount;
	private Long leftQueueCount;
}
