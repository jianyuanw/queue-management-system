package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
}
