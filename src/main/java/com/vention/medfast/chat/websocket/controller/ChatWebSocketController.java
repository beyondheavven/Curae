package com.vention.medfast.chat.websocket.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.mapper.ChatMapper;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.chat.websocket.dto.ChatMessageSendWsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

	private final ChatService chatService;

	private final ChatMapper chatMapper;

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chats/{chatId}/send")
	public void sendMessage(@DestinationVariable Long chatId, Principal principal, @Valid ChatMessageSendWsDto dto) {
		if (!(principal instanceof Authentication authentication)
				|| !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
			throw new AccessDeniedException("Unauthenticated WebSocket message");
		}

		Message message = chatService.sendMessage(chatId, user.getId(), dto.content());

		MessageResponseDto response = chatMapper.toMessageResponse(message);

		messagingTemplate.convertAndSend("/topic/chats/" + chatId, response);
	}

}
