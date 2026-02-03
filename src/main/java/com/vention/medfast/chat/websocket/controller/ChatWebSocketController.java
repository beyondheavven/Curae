package com.vention.medfast.chat.websocket.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.chat.websocket.dto.ChatMessageSendWsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

	private final ChatService chatService;

	@MessageMapping("/chats/{chatId}/send")
	public void sendMessage(@DestinationVariable Long chatId, @AuthenticationPrincipal CustomUserDetails user,
			@Valid ChatMessageSendWsDto dto) {
		if (user == null) {
			throw new AccessDeniedException("Unauthenticated WebSocket message");
		}

		chatService.createMessage(chatId, user.getId(), dto.content());
	}

}
