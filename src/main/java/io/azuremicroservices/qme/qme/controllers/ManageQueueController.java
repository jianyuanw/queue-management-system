package io.azuremicroservices.qme.qme.controllers;

import java.util.List;

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
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.BranchService;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
@RequestMapping("/manage/queue")
public class ManageQueueController {
	private final QueueService queueService;
	private final PermissionService permissionService;
	private final AlertService alertService;
	private final AccountService accountService;
	private final BranchService branchService;
	
	public ManageQueueController(QueueService queueService,PermissionService permissionService,AlertService alertService,AccountService accountService, BranchService branchService) {
		this.queueService = queueService;
		this.permissionService = permissionService;
		this.alertService = alertService;
		this.accountService = accountService;
		this.branchService = branchService;
	}
	
	@GetMapping("/list")
	public String initManageQueueList(Model model, Authentication authentication) {
		List<Branch> branches = permissionService.getBranchPermissions(((MyUserDetails) authentication.getPrincipal()).getId());
		
		model.addAttribute("branches", branches);
		model.addAttribute("queues", queueService.findAllQueuesInBranches(branches));
		return "manage/queue/list";
	}
	
	@GetMapping("/create")
	public String initCreateQueueForm(Model model, Authentication authentication) {
		List<Branch> branches = permissionService.getBranchPermissions(((MyUserDetails) authentication.getPrincipal()).getId());
		model.addAttribute("branches", branches);
		model.addAttribute("queue", new Queue());
		return "manage/queue/create";
	}
	
	@PostMapping("/create")
	public String createQueue(Model model, @ModelAttribute @Valid Queue queue, BindingResult bindingResult, Authentication authentication, RedirectAttributes redirAttr, String branchId) {
		if (bindingResult.hasErrors()) {
			List<Branch> branches = permissionService.getBranchPermissions(((MyUserDetails) authentication.getPrincipal()).getId());
			model.addAttribute("branches", branches);			
			return "manage/queue/create";
		}
		
		queue.setBranch(branchService.findBranchById(Long.parseLong(branchId)).get());
		queueService.createQueue(queue);
		queueService.addQueueIdToQueueEmittersMap(queue.getId());
		alertService.createAlert(AlertColour.GREEN, "Queue successfully created", redirAttr);
		return "redirect:/manage/queue/list";
	}
	
	@GetMapping("/update/{queueId}")
	public String initUpdateQueueForm(Model model, @PathVariable("queueId") Long queueId, Authentication authentication, RedirectAttributes redirAttr) {
		var queue = queueService.findQueueById(queueId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (queue.isEmpty() || !permissionService.authenticateBranch(accountService.findUserByUsername(user.getUsername()), queue.get().getBranch())) {
			alertService.createAlert(AlertColour.YELLOW, "Queue could not be found", redirAttr);			
			return "redirect:/manage/queue/list";
		}
		
		model.addAttribute("queue", queue.get());
		return "manage/queue/update";
	}
	
	@PostMapping("/update")
	public String updateQueue(@ModelAttribute @Valid Queue queue, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/queue/update";
		} 

		queueService.updateQueue(queue);
		alertService.createAlert(AlertColour.GREEN, "Queue successfully updated", redirAttr);
		return "redirect:/manage/queue/list";
	}
	
	@GetMapping("/delete/{queueId}")
	public String deleteBranch(@PathVariable("queueId") Long queueId, Authentication authentication, RedirectAttributes redirAttr) {
		var queue = queueService.findQueueById(queueId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (queue.isEmpty() || !permissionService.authenticateBranch(accountService.findUserByUsername(user.getUsername()), queue.get().getBranch())) {
			alertService.createAlert(AlertColour.YELLOW, "Queue could not be found", redirAttr);
		} else {
			queueService.deleteQueue(queue.get());
			alertService.createAlert(AlertColour.GREEN, "Queue successfully deleted", redirAttr);			
		}
		
		return "redirect:/manage/queue/list";
	}

}
