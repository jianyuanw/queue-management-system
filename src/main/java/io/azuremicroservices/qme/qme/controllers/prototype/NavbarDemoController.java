package io.azuremicroservices.qme.qme.controllers.prototype;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class NavbarDemoController {

    @GetMapping("/prototype/navbar")
    public String initNavbarPrototypeInterface() {
        return "prototype/navbardemo";
    }
}
