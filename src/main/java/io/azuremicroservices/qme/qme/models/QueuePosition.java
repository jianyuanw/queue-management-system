package io.azuremicroservices.qme.qme.models;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class QueuePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Queue queue;

    @ManyToOne
    private User user;

    private String queueNumber;

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
        INACTIVE_LEFT;

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

}
