package com.vention.medfast.chat.rabbitmq.listener;

import com.vention.medfast.chat.rabbitmq.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rabbit.enabled", havingValue = "true")
public class ChatMessageListener {

	private final SimpMessagingTemplate messagingTemplate;

	@RabbitListener(queues = "#{chatQueue.name}")
	public void handle(ChatMessageEvent event) {
		log.info("Instance {} received message for chat {}", System.getenv("HOSTNAME"), event.getChatId());

		messagingTemplate.convertAndSend("/topic/chats/" + event.getChatId(), event);
	}

}
