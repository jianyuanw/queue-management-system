package io.azuremicroservices.qme.qme.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Branch branch;

    private String name;

    private String description;

    private State state;

    private Double timePerClient;

    private Integer notificationPosition;

    private Double notificationDelay;

    @OneToMany(mappedBy = "queue")
    private List<QueuePosition> queuePositions;

    enum State {
        OPEN,
        CLOSED,
        UNKNOWN;

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
