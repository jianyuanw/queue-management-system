package io.azuremicroservices.qme.qme.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Counter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	@ManyToOne
	@Exclude
	private Queue queue;
	
	@OneToOne
	@Exclude
	private User user;
	
	@OneToOne
	@Exclude
	private QueuePosition currentlyServingQueueNumber;

	@NotEmpty(message = "Counter name must not be empty")
	@Pattern(regexp = "[A-Za-z0-9 ]+", message = "Counter name must only contain alphanumeric characters and spaces")
	private String name;
	
	@NotEmpty(message = "Counter description must not be empty")
	@Pattern(regexp = "[A-Za-z0-9 ]+", message = "Counter description must only contain alphanumeric characters and spaces")	
	private String description;
	
}
