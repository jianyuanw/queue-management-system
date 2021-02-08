package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String clientLandingPage() {
        return "client/landing-page";
    }
}
