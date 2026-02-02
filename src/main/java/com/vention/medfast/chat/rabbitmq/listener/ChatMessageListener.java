package com.vention.medfast.chat.rabbitmq.listener;

import com.vention.medfast.chat.rabbitmq.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatMessageListener {

	private final SimpMessagingTemplate messagingTemplate;

	@RabbitListener(queues = "#{chatQueue.name}")
	public void handle(ChatMessageEvent event) {
		log.info("Instance {} received message for chat {}", System.getenv("HOSTNAME"), event.getChatId());

		messagingTemplate.convertAndSend("/topic/chats/" + event.getChatId(), event);
	}

}
