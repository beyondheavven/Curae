package com.vention.curae.chat.service;

import com.vention.curae.chat.message.model.Message;
import com.vention.curae.chat.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

	Chat createChat(Long patientId, Long doctorId);

	List<Chat> getUserChats(Long userId);

	Page<Message> getChatMessages(Long chatId, Long userId, Pageable pageable);

	Message createMessage(Long chatId, Long senderId, String content);

}
