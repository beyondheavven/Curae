package com.vention.curae.chat.message.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_messages", indexes = @Index(name = "idx_message_chat_id", columnList = "chat_id"))
@Getter
@Setter
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "chat_id", nullable = false)
	private Long chatId;

	@Column(name = "sender_id", nullable = false)
	private Long senderId;

	@Column(name = "content", nullable = false, length = 2000)
	private String content;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	public static Message create(Long chatId, Long senderId, String content) {
		Message message = new Message();
		message.chatId = chatId;
		message.senderId = senderId;
		message.content = content;
		return message;
	}

}
