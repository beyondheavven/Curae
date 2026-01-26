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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private UserService userService;

	private ChatServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new ChatServiceImpl(chatRepository, messageRepository, userService);
	}

	@Test
	void createChatCreatesNewChatWhenNotExists() {
		User patient = user(1L, RoleType.PATIENT);
		User doctor = user(2L, RoleType.DOCTOR);

		when(userService.findById(1L)).thenReturn(patient);
		when(userService.findById(2L)).thenReturn(doctor);
		when(chatRepository.findByPatientIdAndDoctorId(1L, 2L)).thenReturn(Optional.empty());
		when(chatRepository.save(any(Chat.class))).thenAnswer(inv -> inv.getArgument(0));

		Chat chat = service.createChat(1L, 2L);

		assertThat(chat.getPatientId()).isEqualTo(1L);
		assertThat(chat.getDoctorId()).isEqualTo(2L);

		verify(chatRepository).save(any(Chat.class));
	}

	@Test
	void createChatReturnsExistingChat() {
		Chat existing = Chat.create(1L, 2L);

		when(userService.findById(1L)).thenReturn(user(1L, RoleType.PATIENT));
		when(userService.findById(2L)).thenReturn(user(2L, RoleType.DOCTOR));
		when(chatRepository.findByPatientIdAndDoctorId(1L, 2L)).thenReturn(Optional.of(existing));

		Chat chat = service.createChat(1L, 2L);

		assertThat(chat).isSameAs(existing);
		verify(chatRepository, never()).save(any());
	}

	@Test
	void createChatFailsWhenSameUser() {
		assertThatThrownBy(() -> service.createChat(1L, 1L)).isInstanceOf(ChatOperationException.class)
			.hasMessageContaining("yourself");
	}

	@Test
    void createChatFailsWhenInitiatorNotPatient() {
        when(userService.findById(1L)).thenReturn(user(1L, RoleType.DOCTOR));
        when(userService.findById(2L)).thenReturn(user(2L, RoleType.DOCTOR));

        assertThatThrownBy(() -> service.createChat(1L, 2L))
            .isInstanceOf(ChatOperationException.class)
            .hasMessageContaining("Only patient");
    }

	@Test
    void getUserChatsDelegatesToRepository() {
        when(chatRepository.findAllByPatientIdOrDoctorId(5L, 5L))
            .thenReturn(List.of());

        service.getUserChats(5L);

        verify(chatRepository).findAllByPatientIdOrDoctorId(5L, 5L);
    }

	@Test
    void getChatMessagesFailsWhenChatNotFound() {
        when(chatRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            service.getChatMessages(10L, 1L, Pageable.unpaged())
        ).isInstanceOf(EntityNotFoundException.class);
    }

	@Test
	void getChatMessagesFailsWhenUserNotParticipant() {
		Chat chat = Chat.create(1L, 2L);
		when(chatRepository.findById(5L)).thenReturn(Optional.of(chat));

		assertThatThrownBy(() -> service.getChatMessages(5L, 99L, Pageable.unpaged()))
			.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void getChatMessagesReturnsPage() {
		Chat chat = Chat.create(1L, 2L);
		Page<Message> page = new PageImpl<>(List.of());

		when(chatRepository.findById(5L)).thenReturn(Optional.of(chat));
		when(messageRepository.findByChatId(eq(5L), any(Pageable.class))).thenReturn(page);

		Page<Message> result = service.getChatMessages(5L, 1L, PageRequest.of(0, 10));

		assertThat(result).isSameAs(page);
	}

	@Test
	void createMessageFailsOnBlankContent() {
		assertThatThrownBy(() -> service.createMessage(1L, 1L, "   ")).isInstanceOf(ChatOperationException.class);
	}

	@Test
	void createMessageFailsWhenUserNotParticipant() {
		Chat chat = Chat.create(1L, 2L);
		when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));

		assertThatThrownBy(() -> service.createMessage(10L, 99L, "hello")).isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void createMessageSavesNormalizedMessage() {
		Chat chat = Chat.create(1L, 2L);
		when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
		when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

		Message message = service.createMessage(10L, 1L, "  hello  ");

		assertThat(message.getContent()).isEqualTo("hello");
		assertThat(message.getChatId()).isEqualTo(10L);
		assertThat(message.getSenderId()).isEqualTo(1L);
	}

	private User user(Long id, RoleType role) {
		User user = new User();
		user.setId(id);
		user.setRole(role);
		return user;
	}

}
