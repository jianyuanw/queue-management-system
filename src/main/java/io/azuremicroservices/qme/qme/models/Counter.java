package io.azuremicroservices.qme.qme.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class Counter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Queue queue;
	
	@OneToOne(fetch=FetchType.LAZY)
	private User user;
	
	@OneToOne(fetch=FetchType.LAZY)
	private QueuePosition currentlyServingQueueNumber;
	
	private String name;
	
	private String description;
	
}
