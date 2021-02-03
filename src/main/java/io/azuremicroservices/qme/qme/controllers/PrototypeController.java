package io.azuremicroservices.qme.qme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrototypeController {
	@GetMapping("/")
	public String initPrototypeInterface() {
		return "prototype/main";
	}
}
