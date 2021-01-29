package io.azuremicroservices.qme.qme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FillerController {
    @GetMapping
    public String ReturnIndex() {
        return "base";
    }
}
