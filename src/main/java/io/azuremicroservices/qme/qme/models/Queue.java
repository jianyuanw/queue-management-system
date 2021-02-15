package io.azuremicroservices.qme.qme.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
public class Queue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Branch branch;

	private String name;

	private String description;

	private State state;

	private Double timePerClient;

	private Integer notificationPosition;

	private Double notificationDelay;

	@OneToMany(mappedBy = "queue", cascade = CascadeType.ALL)
	@Exclude
	private List<QueuePosition> queuePositions;

	@OneToMany(mappedBy = "queue", cascade = CascadeType.ALL)
	@Exclude
	private List<UserQueuePermission> userQueuePermissions;
	
	@OneToMany(mappedBy = "queue", cascade = CascadeType.ALL)
	@Exclude
	private List<Counter> counters;

	public enum State {
		OPEN, CLOSED, UNKNOWN;

		private final String displayValue;

		State() {
			// Generalized constructor that converts capitalized enum values to TitleCase
			StringBuilder sb = new StringBuilder();

			for (String word : this.name().split("_")) {
				sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
			}

			this.displayValue = sb.toString().trim();
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	public Queue(Vendor vendor, Branch branch, String name, String description, State state, Double timePerClient,
			Integer notificationPosition, Double notificationDelay) {
		super();
		this.branch = branch;
		this.name = name;
		this.description = description;
		this.state = state;
		this.timePerClient = timePerClient;
		this.notificationPosition = notificationPosition;
		this.notificationDelay = notificationDelay;
	}

}