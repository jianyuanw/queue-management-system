package io.azuremicroservices.qme.qme.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.CounterService;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
@RequestMapping("/manage/counter")
public class ManageCounterController {
	private final CounterService counterService;
	private final PermissionService permissionService;
	private final AlertService alertService;	
	private final AccountService accountService;
	private final QueueService queueService;
	
	@Autowired
	public ManageCounterController(CounterService counterService, PermissionService permissionService, AlertService alertService, AccountService accountService, QueueService queueService) {
		this.counterService = counterService;
		this.permissionService = permissionService;
		this.alertService = alertService;
		this.accountService = accountService;
		this.queueService = queueService;
	}
	
	@GetMapping("/list")
	public String initManageCounterList(Model model, Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		
		List<Queue> queues = permissionService.getQueuePermissions(userDetails.getId());
		
		model.addAttribute("branches", permissionService.getBranchPermissions(userDetails.getId()));
		model.addAttribute("counters", counterService.findAllCountersInQueues(queues));
		
		return "manage/counter/list";
	}
	
	@GetMapping("/create")
	public String initCreateCounterForm(Model model, Authentication authentication) {
		List<Queue> queues = permissionService.getQueuePermissions(((MyUserDetails) authentication.getPrincipal()).getId());
		
		model.addAttribute("queues", queues);
		model.addAttribute("counter", new Counter());
		return "manage/counter/create";
	}
	
	@PostMapping("/create")
	public String createCounter(@ModelAttribute @Valid Counter counter, BindingResult bindingResult, RedirectAttributes redirAttr, String queueId) {
		if (bindingResult.hasErrors()) {
			return "manage/counter/create";
		} 
		counter.setQueue(queueService.findQueueById(Long.parseLong(queueId)).get());
		counterService.createCounter(counter);
		alertService.createAlert(AlertColour.GREEN, "Counter successfully created", redirAttr);
		return "redirect:/manage/counter/list";
	}
	
	@GetMapping("/update/{counterId}")
	public String initUpdateCounterForm(Model model, @PathVariable("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
		var counter = counterService.findCounterById(counterId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (counter.isEmpty() || !permissionService.authenticateQueue(accountService.findUserByUsername(user.getUsername()), counter.get().getQueue())) {
			alertService.createAlert(AlertColour.YELLOW, "Counter could not be found", redirAttr);			
			return "redirect:/manage/counter/list";
		}
		
		model.addAttribute("counter", counter.get());
		return "manage/counter/update";
	}
	
	@PostMapping("/update")
	public String updateCounter(@ModelAttribute @Valid Counter counter, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/counter/update";
		} 

		counterService.updateCounter(counter);
		alertService.createAlert(AlertColour.GREEN, "Counter successfully updated", redirAttr);
		return "redirect:/manage/counter/list";
	}
	
	@GetMapping("/delete/{counterId}")
	public String deleteBranch(@PathVariable("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
		var counter = counterService.findCounterById(counterId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (counter.isEmpty() || !permissionService.authenticateQueue(accountService.findUserByUsername(user.getUsername()), counter.get().getQueue())) {
			alertService.createAlert(AlertColour.YELLOW, "Counter could not be found", redirAttr);
		} else {
			counterService.deleteCounter(counter.get());
			alertService.createAlert(AlertColour.GREEN, "Counter successfully deleted", redirAttr);			
		}
		
		return "redirect:/manage/counter/list";
	}
	
	
}
