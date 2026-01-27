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
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

		CustomUserDetails user = new CustomUserDetails(userId, "test@mail.com", "pass", null);

		Message message = Message.create(chatId, userId, "hello");
		MessageResponseDto responseDto = new MessageResponseDto(1L, chatId, userId, "hello", null);

		when(chatService.createMessage(chatId, userId, "hello")).thenReturn(message);
		when(chatMapper.toMessageResponse(message)).thenReturn(responseDto);

		controller.sendMessage(chatId, user, dto);

		verify(chatService).createMessage(chatId, userId, "hello");
		verify(chatMapper).toMessageResponse(message);
		verify(messagingTemplate).convertAndSend("/topic/chats/" + chatId, responseDto);
	}

	@Test
	void sendMessage_shouldThrowAccessDenied_whenPrincipalIsInvalid() {

		ChatMessageSendWsDto dto = new ChatMessageSendWsDto("hello");

		assertThatThrownBy(() -> controller.sendMessage(1L, null, dto)).isInstanceOf(AccessDeniedException.class);
	}

}
