package io.azuremicroservices.qme.qme.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @Exclude
    private User user;

    private String title;
    
    private String description;
    
    private String response;
    
    private LocalDateTime ticketRaisedTime;
    
    private LocalDateTime responseTime;
    
    private TicketState ticketState;

    public enum TicketState {
        OPEN,
        CLOSED,
        ARCHIVED;

        private final String displayValue;

        TicketState() {
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
