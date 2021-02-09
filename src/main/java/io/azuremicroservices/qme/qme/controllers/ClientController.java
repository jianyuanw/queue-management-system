package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.services.ClientService;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String clientLandingPage(Model model) {
        // TODO: Retrieve categories from db instead of hardcode
        String[] categories = {
                "Healthcare",
                "Finance",
                "Dining",
                "Technology",
                "Retail",
                "Government"
        };
        model.addAttribute("categories", categories);
        return "client/landing-page";
    }

    @GetMapping("/search-result")
    public String searchResult(Model model) {
        List<Branch> branches = clientService.findBranchesBySearchTerm("splash");
        model.addAttribute("branches", branches);
        return "client/search-result";
    }
}
