package com.vention.medfast.chat.rabbitmq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {

	private Long chatId;

	private Long messageId;

	private Long senderId;

	private String content;

	private Instant createdAt;

}
