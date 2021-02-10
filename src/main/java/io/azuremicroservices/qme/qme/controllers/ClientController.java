package io.azuremicroservices.qme.qme.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.ClientService;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final AlertService alertService;

    public ClientController(ClientService clientService, AlertService alertService) {
        this.clientService = clientService;
        this.alertService = alertService;
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
    		return "redirect:/client/landing-page";
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
}
