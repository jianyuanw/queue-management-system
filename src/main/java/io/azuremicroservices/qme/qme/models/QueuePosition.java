package io.azuremicroservices.qme.qme.models;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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

    private State state;

    private LocalDateTime queueStartTime;

    private LocalDateTime queueEndTime;

    private LocalDateTime stateChangeTime;
    //this is for estimated waiting time prototype
    public QueuePosition(LocalDateTime startTime, LocalDateTime endTime) {
    }

    enum State {
        QUEUE,
        PROCESSED_PRESENT,
        PROCESSED_ABSENT,
        REJOIN_QUEUE;

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
