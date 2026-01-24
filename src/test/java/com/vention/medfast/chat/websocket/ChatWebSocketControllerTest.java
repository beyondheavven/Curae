package com.vention.medfast.chat.websocket;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.mapper.ChatMapper;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.chat.websocket.controller.ChatWebSocketController;
import com.vention.medfast.chat.websocket.dto.ChatMessageSendWsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Principal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

		ChatMessageSendWsDto dto = new ChatMessageSendWsDto("hello");

		CustomUserDetails userDetails = new CustomUserDetails(userId, "test@mail.com", "pass", null);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

		Message message = Message.create(chatId, userId, "hello");
		MessageResponseDto responseDto = new MessageResponseDto(1L, chatId, userId, "hello", null);

		when(chatService.sendMessage(chatId, userId, "hello")).thenReturn(message);
		when(chatMapper.toMessageResponse(message)).thenReturn(responseDto);

		controller.sendMessage(chatId, authentication, dto);

		verify(chatService).sendMessage(chatId, userId, "hello");
		verify(chatMapper).toMessageResponse(message);
		verify(messagingTemplate).convertAndSend("/topic/chats/" + chatId, responseDto);
	}

	@Test
	void sendMessage_shouldThrowAccessDenied_whenPrincipalIsInvalid() {
		Principal invalidPrincipal = () -> "anonymous";

		ChatMessageSendWsDto dto = new ChatMessageSendWsDto("hello");

		assertThatThrownBy(() -> controller.sendMessage(1L, invalidPrincipal, dto))
			.isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
	}

}
