package io.azuremicroservices.qme.qme.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String body;
	
	private LocalDateTime timeOfMessage;
	
	@ManyToOne
	private User user;
	
	private MessageState state;
	
	public enum MessageState {
		NEW,
		SEEN,
		ARCHIVED;
	}
}
