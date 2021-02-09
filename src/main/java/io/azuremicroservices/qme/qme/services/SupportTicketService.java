package io.azuremicroservices.qme.qme.services;

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
		supportTicketRepo.save(supportTicket);
	}
	
	public List<SupportTicket> viewMySupportTickets(User user){
		return supportTicketRepo.findAllByUser(user);
	}
	
	public SupportTicket viewSupportTicket(Long id) {
		return supportTicketRepo.findById(id).get();
	}
	
	public List<SupportTicket> viewSupportTickets(){
		return supportTicketRepo.findAll();
	}
	
	public void updateSupportTicket(SupportTicket supportTicket) {
		supportTicket.setTicketState(TicketState.CLOSED);
		supportTicketRepo.save(supportTicket);
	}
	
	public Optional<SupportTicket> findSupportTicket(Long id){
		return supportTicketRepo.findById(id);
	}
	
	public void deleteSupportTicket(SupportTicket supportTicket) {
		supportTicketRepo.delete(supportTicket);
	}

}
