package com.vention.medfast.chat.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.dto.ChatCreateRequestDto;
import com.vention.medfast.chat.dto.ChatResponseDto;
import com.vention.medfast.chat.mapper.ChatMapper;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.dto.MessageSendRequestDto;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.model.Chat;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

	@Mock
	private ChatService chatService;

	@Mock
	private ChatMapper chatMapper;

	private ChatController controller;

	@BeforeEach
	void setUp() {
		controller = new ChatController(chatService, chatMapper);
	}

	@Test
	void createChatReturnsCreatedResponse() {
		CustomUserDetails user = new CustomUserDetails(1L, "patient@mail.com", "pwd", RoleType.PATIENT);

		ChatCreateRequestDto request = new ChatCreateRequestDto(2L);

		Chat chat = Chat.create(1L, 2L);
		chat.setId(10L);
		chat.setCreatedAt(Instant.now());

		ChatResponseDto responseDto = new ChatResponseDto(10L, 1L, 2L, chat.getCreatedAt());

		when(chatService.createChat(1L, 2L)).thenReturn(chat);
		when(chatMapper.toChatResponse(chat)).thenReturn(responseDto);

		ResponseWrapper<ChatResponseDto> response = controller.createChat(user, request);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.getData()).isEqualTo(responseDto);

		verify(chatService).createChat(1L, 2L);
		verify(chatMapper).toChatResponse(chat);
	}

	@Test
	void getMyChatsReturnsOkResponseWithMappedList() {
		CustomUserDetails user = new CustomUserDetails(5L, "doctor@mail.com", "pwd", RoleType.DOCTOR);

		Chat chat = Chat.create(10L, 5L);
		chat.setId(1L);
		chat.setCreatedAt(Instant.now());

		ChatResponseDto dto = new ChatResponseDto(1L, 10L, 5L, chat.getCreatedAt());

		when(chatService.getUserChats(5L)).thenReturn(List.of(chat));
		when(chatMapper.toChatResponse(chat)).thenReturn(dto);

		ResponseWrapper<List<ChatResponseDto>> response = controller.getMyChats(user);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getData()).containsExactly(dto);

		verify(chatService).getUserChats(5L);
		verify(chatMapper).toChatResponse(chat);
	}

	@Test
	void getMessagesReturnsPagedResponse() {
		CustomUserDetails user = new CustomUserDetails(3L, "patient@mail.com", "pwd", RoleType.PATIENT);

		Pageable pageable = PageRequest.of(0, 20);

		Message message = Message.create(7L, 3L, "hello");
		message.setId(100L);
		message.setCreatedAt(Instant.now());

		Page<Message> page = new PageImpl<>(List.of(message), pageable, 1);

		MessageResponseDto dto = new MessageResponseDto(100L, 7L, 3L, "hello", message.getCreatedAt());

		when(chatService.getChatMessages(7L, 3L, pageable)).thenReturn(page);
		when(chatMapper.toMessageResponse(message)).thenReturn(dto);

		ResponseWrapper<Page<MessageResponseDto>> response = controller.getMessages(user, 7L, pageable);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData().getContent()).containsExactly(dto);

		verify(chatService).getChatMessages(7L, 3L, pageable);
		verify(chatMapper).toMessageResponse(message);
	}

	@Test
	void sendMessageReturnsCreatedResponse() {
		CustomUserDetails user = new CustomUserDetails(9L, "doctor@mail.com", "pwd", RoleType.DOCTOR);

		MessageSendRequestDto request = new MessageSendRequestDto("hello");

		Message message = Message.create(99L, 9L, "hello");
		message.setId(55L);
		message.setCreatedAt(Instant.now());

		MessageResponseDto dto = new MessageResponseDto(55L, 99L, 9L, "hello", message.getCreatedAt());

		when(chatService.sendMessage(99L, 9L, "hello")).thenReturn(message);
		when(chatMapper.toMessageResponse(message)).thenReturn(dto);

		ResponseWrapper<MessageResponseDto> response = controller.sendMessage(user, 99L, request);

		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.getData()).isEqualTo(dto);

		verify(chatService).sendMessage(99L, 9L, "hello");
		verify(chatMapper).toMessageResponse(message);
	}

}
