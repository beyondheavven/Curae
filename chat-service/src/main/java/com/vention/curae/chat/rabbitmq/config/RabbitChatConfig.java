package com.vention.curae.chat.rabbitmq.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rabbit.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitChatConfig {

	public static final String CHAT_EXCHANGE = "chat.exchange";

	@Bean
	public FanoutExchange chatExchange() {
		return new FanoutExchange(CHAT_EXCHANGE, true, false);
	}

	@Bean
	public Queue chatQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Binding chatBinding(Queue chatQueue, FanoutExchange chatExchange) {
		return BindingBuilder.bind(chatQueue).to(chatExchange);
	}

	@Bean
	public MessageConverter jacksonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}

}
