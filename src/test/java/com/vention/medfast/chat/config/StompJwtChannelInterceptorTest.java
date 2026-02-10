package com.vention.medfast.chat.config;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.jwt.JwtAuthenticationFilter;
import com.vention.medfast.auth.service.AuthService;
import com.vention.medfast.chat.model.Chat;
import com.vention.medfast.chat.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StompJwtChannelInterceptorTest {

	@Mock
	ChatRepository chatRepository;

	@Mock
	AuthService authService;

	@InjectMocks
	StompJwtChannelInterceptor interceptor;

	@Mock
	MessageChannel channel;

	private Message<byte[]> stompMessage(StompCommand command, String destination, Authentication auth,
			String authorizationHeader) {
		StompHeaderAccessor accessor = StompHeaderAccessor.create(command);

		if (destination != null) {
			accessor.setDestination(destination);
		}

		if (auth != null) {
			accessor.setUser(auth);
		}

		if (authorizationHeader != null) {
			accessor.addNativeHeader("Authorization", authorizationHeader);
		}

		return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
	}

	@Test
	void connectWithoutAuthorizationHeader_shouldThrow() {
		Message<byte[]> message = stompMessage(StompCommand.CONNECT, null, null, null);

		assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(AccessDeniedException.class)
			.hasMessage("Missing Authorization header");
	}

	@Test
	void connectWithBearerToken_shouldAuthenticate() {
		Authentication authentication = new UsernamePasswordAuthenticationToken("user", null);

		when(authService.authenticateByAccessToken("token")).thenReturn(authentication);

		Message<byte[]> message = stompMessage(StompCommand.CONNECT, null, null,
				JwtAuthenticationFilter.BEARER_PREFIX + "token");

		interceptor.preSend(message, channel);

		verify(authService).authenticateByAccessToken("token");
	}

	@Test
	void subscribeWithoutAuthentication_shouldThrow() {
		Message<byte[]> message = stompMessage(StompCommand.SUBSCRIBE, "/topic/chats/1", null, null);

		assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(AccessDeniedException.class)
			.hasMessage("Unauthenticated WebSocket subscription");
	}

	@Test
	void subscribeWhenUserNotChatParticipant_shouldThrow() {
		CustomUserDetails user = mock(CustomUserDetails.class);
		when(user.getId()).thenReturn(99L);

		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

		Chat chat = new Chat();
		chat.setPatientId(1L);
		chat.setDoctorId(2L);

		when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

		Message<byte[]> message = stompMessage(StompCommand.SUBSCRIBE, "/topic/chats/1", auth, null);

		assertThatThrownBy(() -> interceptor.preSend(message, channel)).isInstanceOf(AccessDeniedException.class)
			.hasMessage("You are not a participant of this chat");
	}

}
