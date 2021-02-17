package io.azuremicroservices.qme.qme.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.SupportTicket;
import io.azuremicroservices.qme.qme.models.SupportTicket.TicketState;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.repositories.SupportTicketRepository;


@Service
public class SupportTicketService {
	private final SupportTicketRepository supportTicketRepo;
	
	@Autowired
	public SupportTicketService(SupportTicketRepository supportTicketRepo) {
		this.supportTicketRepo = supportTicketRepo;
	}
	
	public void createSupportTicket(SupportTicket supportTicket) {
		supportTicket.setResponse(null);
		supportTicket.setTicketState(TicketState.OPEN);
		supportTicket.setTicketRaisedTime(LocalDateTime.now());
		supportTicket.setResponseTime(null);
		supportTicketRepo.save(supportTicket);
	}
	
	public List<SupportTicket> viewMySupportTickets(User user){
		return supportTicketRepo.findAllByUserAndTicketStateNot(user, TicketState.ARCHIVED);
	}
	
	public SupportTicket viewSupportTicket(Long id) {
		return supportTicketRepo.findById(id).get();
	}
	
	public List<SupportTicket> viewSupportTickets(){
		return supportTicketRepo.findAll();
	}
	
	public void updateSupportTicket(SupportTicket supportTicket) {
		supportTicket.setTicketState(TicketState.CLOSED);
		supportTicket.setResponseTime(LocalDateTime.now());
		supportTicketRepo.save(supportTicket);
	}
	
	public Optional<SupportTicket> findSupportTicket(Long id){
		return supportTicketRepo.findById(id);
	}
	
	public void deleteSupportTicket(SupportTicket supportTicket) {
		supportTicketRepo.delete(supportTicket);
	}

	public boolean archiveSupportTicket(Long ticketId, User user) {
		Optional<SupportTicket> supportTicket = supportTicketRepo.findById(ticketId);
		
		if (supportTicket.isEmpty() || supportTicket.get().getUser().getId() != user.getId() || supportTicket.get().getTicketState() != TicketState.CLOSED) {
			return false;
		}
		
		SupportTicket ticket = supportTicket.get();
		ticket.setTicketState(TicketState.ARCHIVED);
		supportTicketRepo.save(ticket);
		return true;
	}

}
