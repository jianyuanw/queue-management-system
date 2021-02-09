package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.SupportTicket;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.SupportTicketService;

@Controller
@RequestMapping("/support-ticket")
public class SupportTicketController {
	private final SupportTicketService supportTicketService;
	private final AlertService alertService;
	
	public SupportTicketController(SupportTicketService supportTicketService, AlertService alertService) {
		this.supportTicketService = supportTicketService;
		this.alertService = alertService;
	}
	
	@GetMapping("/my-tickets")
	public String MySupportTickets(Model model, Authentication authentication) {
		User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
		model.addAttribute("myTickets",supportTicketService.viewMySupportTickets(user));
		return "support-ticket/list";
	}
	
	@GetMapping("/create")
	public String SupportTicketForm(Model model){
		model.addAttribute("supportTicket",new SupportTicket());
		return "support-ticket/create";		
	}
	
	@PostMapping("/create")
	public String CreateSupportTicket(@ModelAttribute @Valid SupportTicket supportTicket, BindingResult bindingResult, RedirectAttributes redirAttr, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "/support-ticket/create";
		} else {
			User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
			supportTicket.setUser(user);
			supportTicketService.createSupportTicket(supportTicket);
		}
		alertService.createAlert(AlertColour.GREEN, "Ticket successfully raised", redirAttr);
		return "redirect:/support-ticket/my-tickets";
	}
	
	@GetMapping("/view/{supportTicketId}")
	public String ViewSupportTicket(Model model, @PathVariable("supportTicketId") Long supportTicketId) {
		model.addAttribute("supportTicket", supportTicketService.viewSupportTicket(supportTicketId));
		return "/support-ticket/view";	
	}
	
}
