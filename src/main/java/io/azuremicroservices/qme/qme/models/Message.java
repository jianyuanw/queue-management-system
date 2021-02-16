package io.azuremicroservices.qme.qme.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class Message {
	private String body;
	private LocalDateTime timeofMessage;
	
	@OneToOne(mappedBy = "message")
	private SupportTicket supportTicket;
}
