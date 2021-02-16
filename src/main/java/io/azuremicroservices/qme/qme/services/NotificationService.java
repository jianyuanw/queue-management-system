package io.azuremicroservices.qme.qme.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Message;
import io.azuremicroservices.qme.qme.models.Message.MessageState;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.repositories.MessageRepository;
import io.azuremicroservices.qme.qme.repositories.SupportTicketRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class NotificationService {
	private final SupportTicketRepository supportTicketRepo;
	private final MessageRepository messageRepo;
	private final UserRepository userRepo;
	private final SMSService smsService;
	
	@Autowired
	public NotificationService(SupportTicketRepository supportTicketRepo, MessageRepository messageRepo, UserRepository userRepo, SMSService smsService) {
		this.supportTicketRepo = supportTicketRepo;
		this.messageRepo = messageRepo;
		this.userRepo = userRepo;
		this.smsService = smsService;
	}

	@Transactional
	public List<Message> generateNotifications(Long userId) {
		List<MessageState> wantedMessages = new ArrayList<>();
		wantedMessages.add(MessageState.NEW);
		wantedMessages.add(MessageState.SEEN);
		
		List<Message> messages = messageRepo.findAllByUser_IdAndStateInOrderByTimeOfMessageDesc(userId, wantedMessages);
		
		for (Message message : messages) {
			message.setState(MessageState.SEEN);
		}
		
		return messages;
	}

	@Transactional
	public boolean addNotification(Long userId, String title, String body) {
		Optional<User> user = userRepo.findById(userId);
		
		if (user.isPresent()) {	
			// TODO: Uncomment this line for deployment
//			String phoneNumber = user.get().getHandphoneNo();
//			if (!phoneNumber.startsWith("+")) {
//				phoneNumber = "+65" + phoneNumber;
//			}			
//			smsService.send(phoneNumber, new StringBuilder(title).append("\n\n").append(body).toString());
			Message message = new Message();
			message.setUser(user.get());
			message.setTitle(title);
			message.setBody(body);
			message.setTimeOfMessage(LocalDateTime.now());
			message.setState(MessageState.NEW);
			messageRepo.save(message);
			return true;
		}
		
		return false;
	}

	public Integer getNewNotifications(Long userId) {
		return messageRepo.countByUser_IdAndStateEquals(userId, MessageState.NEW);
	}

	public boolean archiveNotification(Long userId, Long messageId) {
		var message = messageRepo.findById(messageId);
		
		if (message.isEmpty() || message.get().getUser().getId() != userId) {
			return false;
		}
		
		Message m = message.get();
		m.setState(MessageState.ARCHIVED);
		
		messageRepo.save(m);
		
		return true;
	}
	
}
