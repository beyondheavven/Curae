package com.vention.curae.chat.service;

import com.vention.curae.api.exception.ChatOperationException;
import com.vention.curae.chat.message.model.Message;
import com.vention.curae.chat.message.repository.MessageRepository;
import com.vention.curae.chat.model.Chat;
import com.vention.curae.chat.rabbitmq.event.ChatMessageEvent;
import com.vention.curae.chat.rabbitmq.publisher.ChatMessagePublisher;
import com.vention.curae.chat.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;

	private final MessageRepository messageRepository;

	private final ObjectProvider<ChatMessagePublisher> chatMessagePublisher;

	@Override
	@Transactional
	public Chat createChat(Long patientId, Long doctorId) {
		if (patientId.equals(doctorId)) {
			throw new ChatOperationException("Cannot create chat with yourself");
		}

		return chatRepository.findByPatientIdAndDoctorId(patientId, doctorId)
			.orElseGet(() -> chatRepository.save(Chat.create(patientId, doctorId)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Chat> getUserChats(Long userId) {
		return chatRepository.findAllByPatientIdOrDoctorId(userId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Message> getChatMessages(Long chatId, Long userId, Pageable pageable) {
		Chat chat = getChatOrThrow(chatId);
		validateChatParticipant(chat, userId);

		return messageRepository.findByChatId(chatId, pageable);
	}

	@Override
	public Message createMessage(Long chatId, Long senderId, String content) {
		String normalizedContent = normalizeMessageContent(content);

		Chat chat = getChatOrThrow(chatId);
		validateChatParticipant(chat, senderId);

		Message message = messageRepository.save(Message.create(chatId, senderId, normalizedContent));

		chatMessagePublisher.ifAvailable(publisher -> publisher.publish(new ChatMessageEvent(message.getChatId(),
				message.getId(), message.getSenderId(), message.getContent(), message.getCreatedAt())));

		return message;
	}

	private void validateChatParticipant(Chat chat, Long userId) {
		if (!chat.getPatientId().equals(userId) && !chat.getDoctorId().equals(userId)) {
			throw new AccessDeniedException("You are not a participant of this chat");
		}
	}

	private Chat getChatOrThrow(Long chatId) {
		return chatRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
	}

	private String normalizeMessageContent(String content) {
		if (content == null || content.isBlank()) {
			throw new ChatOperationException("Message content cannot be empty");
		}

		return content.trim();
	}

}
