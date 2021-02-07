package io.azuremicroservices.qme.qme.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.azuremicroservices.qme.qme.models.SMS;
import io.azuremicroservices.qme.qme.services.SMSService;

@Controller
public class SMSController {
	@Autowired
	SMSService smsService;
	
	// Proof of concept SMSController, trial account can only send to verified
	// numbers, please do not spam or else my account will go broke
	@GetMapping("/sms/{number}")
	public String sendSms(@PathVariable("number") String number) {
		SMS sms = new SMS();
		sms.setTo("+6591003555");
		sms.setMessage("Test message");
		smsService.send(sms);
		return "redirect:/";
	}
}
