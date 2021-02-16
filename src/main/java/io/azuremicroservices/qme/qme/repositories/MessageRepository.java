package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.azuremicroservices.qme.qme.models.Message;
import io.azuremicroservices.qme.qme.models.Message.MessageState;

public interface MessageRepository extends JpaRepository<Message, Long> {
	Integer countByUser_IdAndStateEquals(Long userId, MessageState state);

	List<Message> findAllByUser_IdAndStateInOrderByTimeOfMessageDesc(Long userId, List<MessageState> wantedMessages);

}
