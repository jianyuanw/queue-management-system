package io.azuremicroservices.qme.qme.models;

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
