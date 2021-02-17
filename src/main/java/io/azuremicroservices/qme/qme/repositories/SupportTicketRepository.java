package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.SupportTicket;
import io.azuremicroservices.qme.qme.models.SupportTicket.TicketState;
import io.azuremicroservices.qme.qme.models.User;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
	List<SupportTicket> findAllByUser(User user);

	List<SupportTicket> findAllByUserAndTicketState(User user, TicketState state);

	List<SupportTicket> findAllByUserAndTicketStateNot(User user, TicketState state);
}
