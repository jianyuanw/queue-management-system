package io.azuremicroservices.qme.qme.models;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table(name="queue_position")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QueuePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    private Queue queue;

    @ManyToOne
    @Exclude
    private User user;
    
    @OneToOne(mappedBy = "currentlyServingQueueNumber")
    @Exclude
    private Counter counter;

    private String queueNumber;
    
    private Integer position;
    
    private Integer priority;

    @Enumerated
    private State state;

    private LocalDateTime queueStartTime;

    private LocalDateTime queueEndTime;

    private LocalDateTime stateChangeTime;

    public QueuePosition(Queue queue, String queueNumber, State state, LocalDateTime queueStartTime) {
        this.queue = queue;
        this.queueNumber = queueNumber;
        this.state = state;
        this.queueStartTime = queueStartTime;
    }

    //this is for estimated waiting time prototype
    public QueuePosition(LocalDateTime startTime, LocalDateTime endTime) {
    }

    public enum State {
        ACTIVE_QUEUE,
        ACTIVE_REQUEUE,
        INACTIVE_COMPLETE,
        INACTIVE_NO_SHOW,
        INACTIVE_LEFT,
    	INACTIVE_CALLED;

        private final String displayValue;

        State() {
            // Generalized constructor that converts capitalized enum values to TitleCase
            StringBuilder sb = new StringBuilder();

            for (String word : this.name().split("_")) {
                sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
            }

            this.displayValue = sb.toString().trim();
        }

        public String getDisplayValue() { return displayValue; }
    }
    
    public static List<State> getQueuingStates() {
    	List<State> queueStates = new ArrayList<>();
    	queueStates.add(State.ACTIVE_QUEUE);
    	queueStates.add(State.ACTIVE_REQUEUE);
    	
    	return queueStates;
    }
    
    public static List<State> getViewedStates() {
    	List<State> queueStates = new ArrayList<>();
    	queueStates.add(State.ACTIVE_QUEUE);
    	queueStates.add(State.ACTIVE_REQUEUE);
    	queueStates.add(State.INACTIVE_NO_SHOW);
    	
    	return queueStates;
    }

}
