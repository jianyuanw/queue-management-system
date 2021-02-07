package io.azuremicroservices.qme.qme.models;

import lombok.Data;

@Data
public class Alert {
	private String type;
	private String message;
	
	public Alert(String type, String message) {
		this.type = type;
		this.message = message;
	}

}
