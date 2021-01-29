package io.azuremicroservices.qme.qme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("controllerdemo")
public class NavbarDemoController {

    @GetMapping
    public String controllerDemo() {
        return "controllerdemo";
    }
}
