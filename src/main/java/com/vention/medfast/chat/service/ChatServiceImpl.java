package com.vention.medfast.chat.service;

import com.vention.medfast.api.exception.ChatOperationException;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.message.repository.MessageRepository;
import com.vention.medfast.chat.model.Chat;
import com.vention.medfast.chat.repository.ChatRepository;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;

	private final MessageRepository messageRepository;

	private final UserService userService;

	@Override
	@Transactional
	public Chat createChat(Long patientId, Long doctorId) {
		if (patientId.equals(doctorId)) {
			throw new ChatOperationException("Cannot create chat with yourself");
		}

		User patient = userService.findById(patientId);
		User doctor = userService.findById(doctorId);

		validateChatInitiator(patient, doctor);

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

		Message message = Message.create(chatId, senderId, normalizedContent);
		return messageRepository.save(message);
	}

	private void validateChatInitiator(User patient, User doctor) {
		if (patient.getRole() != RoleType.PATIENT) {
			throw new ChatOperationException("Only patient can initiate chat");
		}
		if (doctor.getRole() != RoleType.DOCTOR) {
			throw new ChatOperationException("Chat can be created only with doctor");
		}
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
		if (StringUtils.isBlank(content)) {
			throw new ChatOperationException("Message content cannot be empty");
		}

		return content.trim();
	}

}
