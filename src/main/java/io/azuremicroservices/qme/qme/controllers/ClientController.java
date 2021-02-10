package io.azuremicroservices.qme.qme.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.ViewQueue;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.ClientService;
import io.azuremicroservices.qme.qme.services.ManageUserQueueService;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final AlertService alertService;
    private final ManageUserQueueService manageUserQueueService;

    public ClientController(ClientService clientService, AlertService alertService, ManageUserQueueService manageUserQueueService) {
        this.clientService = clientService;
        this.alertService = alertService;
        this.manageUserQueueService = manageUserQueueService;
    }

    @GetMapping
    public String clientLandingPage(Model model) {
    	List<String> categories = new ArrayList<>();

    	for (BranchCategory branchCategory : BranchCategory.values()) {
    		categories.add(branchCategory.getDisplayValue());
    	}

        model.addAttribute("categories", categories);
        return "client/landing-page";
    }

    @GetMapping("/search")
    public String searchResult(@RequestParam(required = false) String query, @RequestParam(required = false) String category, Model model, RedirectAttributes redirAttr) {
    	String messageQuery = "";
    	List<Branch> branches;
    	if (query == null && category == null) {
    		alertService.createAlert(AlertColour.YELLOW, "Needs to have at least one search term", redirAttr);
    		return "redirect:/client";
    	} else if (category == null) {
    		branches = clientService.findBranchesByQuery(query);
    		messageQuery = "Search: " + query;
    	} else if (query == null) {
    		branches = clientService.findBranchesByCategory(category);
    		messageQuery = "Category: " + category;
    	} else {
    		branches = clientService.findBranchesByQueryAndCategory(query, category);
    		messageQuery = "Category: " + category + ", Search: " + query;
    	}

        if (branches.size() == 0) {
            model.addAttribute("error", "Unfortunately, there are no results found. Please try another search term.");
        }
        model.addAttribute("branches", branches);
        model.addAttribute("query", messageQuery);
        return "client/search-result";
    }
    
    @GetMapping("/branch/{branchId}")
    public String viewBranch(Model model, @PathVariable("branchId") String branchId, RedirectAttributes redirAttr) {
    	List<Queue> queues = clientService.findQueuesByBranchId(branchId);
    	
    	if (queues == null || queues.size() == 0) {
    		alertService.createAlert(AlertColour.YELLOW, "Branch not found", redirAttr);
    	}
    	
    	List<ViewQueue> viewQueues = clientService.generateViewQueues(queues);
    	
    	model.addAttribute("branch", clientService.findBranchById(branchId));
    	model.addAttribute("viewQueues", viewQueues);
    	return "client/branch";
    }
    
    @PostMapping("/join-queue")
    public String joinQueue(@RequestParam("queueId") String queueId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	
    	manageUserQueueService.enterQueue(myUserDetails.getId().toString(), queueId);
    	
    	alertService.createAlert(AlertColour.GREEN, "Successfully entered queue", redirAttr);
    	return "redirect:/client";
    }

    @GetMapping("/search/branch")
    public String viewBranchQueues(@RequestParam String id, Model model) {
        Long branchId = Long.valueOf(id);
        Branch branch = clientService.findBranchById(branchId);
        List<Queue> queues = clientService.findQueuesByBranchId(branchId);
        model.addAttribute("branch", branch);
        model.addAttribute("queues", queues);
        return "client/branch-queues";
    }
}
