package io.azuremicroservices.qme.qme.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.Alert;

@Service
public class AlertService {
	
	public void createAlert(AlertColour colour, String message, RedirectAttributes redirAttr) {
		redirAttr.addFlashAttribute("alert", new Alert(colour.getDisplayValue(), message));
	}
	
	public enum AlertColour {
		BLUE("primary"),
		GREY("secondary"),
		GREEN("success"),
		RED("danger"),
		YELLOW("warning"),
		TEAL("info"),
		LIGHT_GREY("light"),
		DARK_GREY("dark"),;

		private final String displayValue;
		
		AlertColour(String displayValue) {
			this.displayValue = displayValue;
		}
		
		public String getDisplayValue() { return displayValue; }
		
	}
	
}