package io.azuremicroservices.qme.qme.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;

    private String description;

    private State state;

    enum State {
        OPEN,
        CLOSED;

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
}
