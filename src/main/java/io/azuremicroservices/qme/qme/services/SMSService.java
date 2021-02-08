package io.azuremicroservices.qme.qme.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import io.azuremicroservices.qme.qme.models.SMS;

import com.twilio.Twilio;
import org.springframework.stereotype.Component;

@Component
public class SMSService {
	
	private static final String ACCOUNT_SID = "AC64c06441f00080e8a3a1ac49927cc264";
	private static final String AUTH_TOKEN = "d4290b0c34c4962ee4114e6e6ac7d4eb";    
    private static final String FROM_NUMBER = "+14159680152";

    public void send(String recipientNumber, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(new PhoneNumber(recipientNumber), new PhoneNumber(FROM_NUMBER), messageBody)
                .create();
        // Use message.getSid() if you need the unique ID required to follow up with the transaction

    }
    
    public void send(SMS sms) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(new PhoneNumber(sms.getTo()), new PhoneNumber(FROM_NUMBER), sms.getMessage())
                .create();
        // Use message.getSid() if you need the unique ID required to follow up with the transaction

    }    
}
