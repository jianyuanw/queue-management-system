package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

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
import io.azuremicroservices.qme.qme.services.SupportTicketService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;

@Controller
@RequestMapping("/manage/support-ticket")
public class ManageSupportTicketController {
	private final SupportTicketService supportTicketService;
	
	public ManageSupportTicketController(SupportTicketService supportTicketService) {
		this.supportTicketService = supportTicketService;
	}
	
	@GetMapping("/list")
	public String ViewSupportTickets(Model model) {
		model.addAttribute("tickets", supportTicketService.viewSupportTickets());
		return "manage/support-ticket/list";
	}
	
	@GetMapping("/reply/{supportTicketId}")
	public String ReplySupportTicketForm(Model model, @PathVariable("supportTicketId") Long supportTicketId, RedirectAttributes redirAttr) {
		model.addAttribute("supportTicket", supportTicketService.viewSupportTicket(supportTicketId));
		return "manage/support-ticket/reply";
	}
	
	@PostMapping("/reply")
	public String ReplySupportTicket(@ModelAttribute @Valid SupportTicket supportTicket, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/support-ticket/reply";
		} else {
			supportTicketService.updateSupportTicket(supportTicket);
		}
		return "redirect:/manage/support-ticket/list";
	}
	
	@GetMapping("/delete/{supportTicketId}")
	public String deleteVendor(@PathVariable("supportTicketId") Long supportTicketId, RedirectAttributes redirAttr) {
		var supportTicket = supportTicketService.findSupportTicket(supportTicketId);

		if (supportTicket.isEmpty()) {
			//alertService.createAlert(AlertColour.YELLOW, "App Admin Account not found", redirAttr);
			return "redirect:/manage/support-ticket/list";			
		} else {
			supportTicketService.deleteSupportTicket(supportTicket.get());
		}
		
		//alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully deleted", redirAttr);
		return "redirect:/manage/support-ticket/list";
	}

}
