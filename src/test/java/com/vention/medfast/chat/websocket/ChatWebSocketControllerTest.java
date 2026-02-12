package com.vention.medfast.chat.websocket;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.mapper.ChatMapper;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.chat.websocket.controller.ChatWebSocketController;
import com.vention.medfast.chat.websocket.dto.ChatMessageSendWsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChatWebSocketControllerTest {

	@Mock
	private ChatService chatService;

	@Mock
	private ChatMapper chatMapper;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private ChatWebSocketController controller;

	@Test
	void sendMessage_shouldSendMessageToTopic_whenUserAuthenticated() {
		Long chatId = 1L;
		Long userId = 10L;

		CustomUserDetails user = new CustomUserDetails(userId, "test@mail.com", "pass", null, null, null);

		ChatMessageSendWsDto dto = new ChatMessageSendWsDto("hello");

		controller.sendMessage(chatId, user, dto);

		verify(chatService).createMessage(chatId, userId, "hello");
	}

	@Test
	void sendMessage_shouldThrowAccessDenied_whenPrincipalIsInvalid() {

		ChatMessageSendWsDto dto = new ChatMessageSendWsDto("hello");

		assertThatThrownBy(() -> controller.sendMessage(1L, null, dto)).isInstanceOf(AccessDeniedException.class);
	}

}
