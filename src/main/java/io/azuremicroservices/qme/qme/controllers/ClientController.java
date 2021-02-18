package io.azuremicroservices.qme.qme.controllers;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.models.BranchQueueDto;
import io.azuremicroservices.qme.qme.models.Message;
import io.azuremicroservices.qme.qme.models.MyQueueDto;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.SupportTicket;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.BranchService;
import io.azuremicroservices.qme.qme.services.NotificationService;
import io.azuremicroservices.qme.qme.services.QueueService;
import io.azuremicroservices.qme.qme.services.SupportTicketService;

@Controller
public class ClientController {

    private final BranchService branchService;
    private final QueueService queueService;    
    private final NotificationService notificationService;
    private final AlertService alertService;
    private final SupportTicketService supportTicketService;

    @Autowired
    public ClientController(BranchService branchService, QueueService queueService,
    		NotificationService notificationService, SupportTicketService supportTicketService, AlertService alertService) {
        this.branchService = branchService;
        this.queueService = queueService;        
        this.supportTicketService = supportTicketService;
        this.notificationService = notificationService;
        this.alertService = alertService;        
    }
    
    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
    	MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
    	model.addAttribute("newNotifications", notificationService.getNewNotifications(userDetails.getId()));
    }

    @GetMapping("/home")
    public String clientLandingPage(Model model, Authentication authentication) {
    	List<BranchCategory> categories = new ArrayList<>();

    	for (BranchCategory branchCategory : BranchCategory.values()) {
    		categories.add(branchCategory);
    	}

        model.addAttribute("categories", categories);
        return "client/landing-page";
    }

    @GetMapping("/search")
    public String searchResult(@RequestParam(required = false) String query, @RequestParam(required = false) String category, Model model, Authentication authentication, RedirectAttributes redirAttr) {
    	String messageQuery = "";
    	List<Branch> branches = new ArrayList<>();
    	if (query == null && category == null) {
    		alertService.createAlert(AlertColour.YELLOW, "Needs to have at least one search term", redirAttr);
    		return "redirect:/client";
    	} else {
    		messageQuery = branchService.parseSearchQuery(query, category, branches);
    	}    		

      if (branches.size() == 0) {
          model.addAttribute("error", "Unfortunately, there are no results found. Please try another search term.");
      }

      model.addAttribute("branches", branches);
      model.addAttribute("query", messageQuery);
      return "client/search-result";
    }
    
    @GetMapping("/branch/{branchId}")
    public String viewBranch(Model model, @PathVariable("branchId") String branchId, @RequestParam(required = false) String joinQueue, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
    	List<Queue> queues = queueService.findQueuesByBranchId(Long.parseLong(branchId));    	
    	
    	if (queues == null || queues.size() == 0) {
    		alertService.createAlert(AlertColour.YELLOW, "Branch not found", redirAttr);
    	}
    	
    	List<BranchQueueDto> viewQueues = queueService.generateBranchQueueDtos(userDetails.getId(), queues);
    	
		model.addAttribute("joinQueue", joinQueue);
    	model.addAttribute("branch", branchService.findBranchById(Long.parseLong(branchId)).get());
    	model.addAttribute("viewQueues", viewQueues);
    	return "client/branch";
    }
    
    @PostMapping("/join-queue")
    public String joinQueue(@RequestParam("queueId") String queueId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	boolean joined = queueService.enterQueue(myUserDetails.getId().toString(), queueId); 
    	
    	if (joined) {
    		alertService.createAlert(AlertColour.GREEN, "Successfully entered queue", redirAttr);
    		queueService.refreshBrowsers(Long.valueOf(queueId));
    	} else {
    		alertService.createAlert(AlertColour.YELLOW, "Queue closed. Failed to rejoin.", redirAttr);
    	}    	
    	
    	return "redirect:/my-queues";
    }
    
    @PostMapping("/leave-queue")
    public String leaveQueue(@RequestParam("queueId") String queueId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	    	
    	queueService.leaveQueue(myUserDetails.getId().toString(), queueId);
		queueService.refreshBrowsers(Long.valueOf(queueId));
    	alertService.createAlert(AlertColour.GREEN, "Successfully left queue", redirAttr);
    	return "redirect:/my-queues";
    }

    @PostMapping("/rejoin-queue")
    public String rejoinQueue(@RequestParam String queuePositionId, RedirectAttributes redirAttr) {
        boolean rejoined = queueService.rejoinQueue(Long.valueOf(queuePositionId));
        if (rejoined) {
			queueService.refreshBrowsers(queueService.findQueueIdByQueuePositionId(queuePositionId));
            alertService.createAlert(AlertColour.GREEN, "Successfully rejoined queue", redirAttr);
        } else {
            alertService.createAlert(AlertColour.YELLOW, "Queue closed. Failed to rejoin.", redirAttr);
        }
        return "redirect:/my-queues";
    }

    @GetMapping("/search/branch")
    public String viewBranchQueues(@RequestParam String id, Model model, RedirectAttributes redirAttr) {
        Long branchId = Long.valueOf(id);
        var branch = branchService.findBranchById(branchId);
        
        if (branch.isEmpty()) {
        	alertService.createAlert(AlertColour.YELLOW, "Branch not found", redirAttr);
        	return "redirect:client/search";
        }
        
        List<Queue> queues = queueService.findQueuesByBranchId(branchId);
        model.addAttribute("branch", branch.get());
        model.addAttribute("queues", queues);
        return "client/branch-queues";
    }

    @GetMapping("/my-queues")
    public String myQueues(Authentication authentication, Model model) {
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getId();
        List<MyQueueDto> myQueueDtos = queueService.generateMyQueueDto(userId);
        model.addAttribute("myQueueDtos", myQueueDtos);
        
        if (myQueueDtos.size() == 0) {
            model.addAttribute("error", "Not in any queue");
        }
        
        return "client/my-queues";
    }
    
    @GetMapping("/notifications")
    public String viewNotifications(Authentication authentication, Model model) {
    	Long userId = ((MyUserDetails) authentication.getPrincipal()).getId();
    	
    	List<Message> notifications = notificationService.generateNotifications(userId);
    	
    	model.addAttribute("notifications", notifications);
    	model.addAttribute("newNotifications", 0);
    	return "client/notifications";
    }
    
    @PostMapping("/notifications")
    public String archiveNotification(@RequestParam("messageId") Long messageId, Authentication authentication, Model model, RedirectAttributes redirAttr) {
    	Long userId = ((MyUserDetails) authentication.getPrincipal()).getId();
    	
    	if (notificationService.archiveNotification(userId, messageId)) { 
    		alertService.createAlert(AlertColour.GREEN, "Message archived", redirAttr);
    	} else {
    		alertService.createAlert(AlertColour.YELLOW, "Message not found", redirAttr);
    	}
    	
    	return "redirect:/notifications";
    }
    
    // TODO: Note - This is for testing, remove after
    @GetMapping("/notifications/add/{title}/{body}")
    public String viewNotifications(@PathVariable("title") String title, @PathVariable("body") String body, Authentication authentication, Model model) {
    	Long userId = ((MyUserDetails) authentication.getPrincipal()).getId();
    	
    	notificationService.addNotification(userId, title, body);
    	    	
    	return "redirect:/home";
    }
    
	@GetMapping("/my-tickets")
	public String mySupportTickets(Model model, Authentication authentication) {
		User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
		
		model.addAttribute("myTickets",supportTicketService.viewMySupportTickets(user));
		return "client/my-tickets";
	}
	
	@PostMapping("/my-tickets")
	public String archiveTicket(@RequestParam("ticketId") Long ticketId, Authentication authentication, RedirectAttributes redirAttr) {
		User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
		
    	if (supportTicketService.archiveSupportTicket(ticketId, user)) { 
    		alertService.createAlert(AlertColour.GREEN, "Ticket archived", redirAttr);
    	} else {
    		alertService.createAlert(AlertColour.YELLOW, "Ticket not found or cannot be archived", redirAttr);
    	}		

		return "redirect:/my-tickets";
	}	
	
	@GetMapping("/create-ticket")
	public String supportTicketForm(Model model){
		model.addAttribute("supportTicket",new SupportTicket());
		
		return "client/create-ticket";		
	}
	
	@PostMapping("/create-ticket")
	public String createSupportTicket(@ModelAttribute @Valid SupportTicket supportTicket, BindingResult bindingResult, RedirectAttributes redirAttr, Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "/client/create-ticket";
		} else {
			User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
			supportTicket.setUser(user);
			supportTicketService.createSupportTicket(supportTicket);
		}
		
		alertService.createAlert(AlertColour.GREEN, "Ticket successfully raised", redirAttr);
		return "redirect:/my-tickets";
	}    
}
