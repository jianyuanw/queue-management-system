package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.SupportTicket;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.SupportTicketService;

@Controller
@RequestMapping("/manage/support-ticket")
public class ManageSupportTicketController {
	private final SupportTicketService supportTicketService;
	
	private final AlertService alertService;
	
	@Autowired
	public ManageSupportTicketController(SupportTicketService supportTicketService, AlertService alertService) {
		this.supportTicketService = supportTicketService;
		this.alertService = alertService;
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
			alertService.createAlert(AlertColour.YELLOW, "Support ticket not found", redirAttr);
			return "redirect:/manage/support-ticket/list";			
		} else {
			supportTicketService.deleteSupportTicket(supportTicket.get());
		}
		
		alertService.createAlert(AlertColour.GREEN, "Support ticket successfully deleted", redirAttr);
		return "redirect:/manage/support-ticket/list";
	}
	
	@GetMapping("/view/{supportTicketId}")
	public String ViewSupportTicket(Model model, @PathVariable("supportTicketId") Long supportTicketId) {
		model.addAttribute("supportTicket", supportTicketService.viewSupportTicket(supportTicketId));
		return "manage/support-ticket/view";
	}

}
