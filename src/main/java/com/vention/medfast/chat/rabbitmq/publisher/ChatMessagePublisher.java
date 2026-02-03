package com.vention.medfast.chat.rabbitmq.publisher;

import com.vention.medfast.chat.rabbitmq.config.RabbitChatConfig;
import com.vention.medfast.chat.rabbitmq.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publish(ChatMessageEvent event) {
		rabbitTemplate.convertAndSend(RabbitChatConfig.CHAT_EXCHANGE, "", event);
	}

}
