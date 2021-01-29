package io.azuremicroservices.qme.qme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class NavbarDemoController {

    @GetMapping("controllerdemo")
    public String controllerDemo() {
        return "controllerdemo";
    }
}
