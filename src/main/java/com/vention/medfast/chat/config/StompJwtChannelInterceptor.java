package com.vention.medfast.chat.config;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.jwt.JwtAuthenticationFilter;
import com.vention.medfast.auth.jwt.service.JwtServiceImpl;
import com.vention.medfast.chat.model.Chat;
import com.vention.medfast.chat.repository.ChatRepository;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

	private static final Pattern CHAT_SUBSCRIBE_PATTERN = Pattern.compile("^/topic/chats/(\\d+)$");

	private final JwtServiceImpl jwtService;

	private final UserRepository userRepository;

	private final ChatRepository chatRepository;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			authenticate(accessor);
		}

		if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
			authorizeSubscription(accessor);
		}

		return message;
	}

	private void authenticate(StompHeaderAccessor accessor) {

		String authHeader = accessor.getFirstNativeHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith(JwtAuthenticationFilter.BEARER_PREFIX)) {
			throw new AccessDeniedException("Missing Authorization header");
		}

		String token = authHeader.substring(JwtAuthenticationFilter.BEARER_PREFIX.length());
		Long userId = jwtService.extractUserId(token);

		if (userId == null) {
			throw new AccessDeniedException("Invalid JWT token");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new AccessDeniedException("User not found"));

		CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
				user.getRole());

		if (!jwtService.isAccessToken(token) || !jwtService.isTokenValid(token, userDetails)) {
			throw new AccessDeniedException("Invalid or expired JWT token");
		}

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		accessor.setUser(authentication);
	}

	private void authorizeSubscription(StompHeaderAccessor accessor) {

		Authentication auth = (Authentication) accessor.getUser();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
			throw new AccessDeniedException("Unauthenticated WebSocket subscription");
		}

		String destination = accessor.getDestination();
		if (destination == null) {
			return;
		}

		Matcher matcher = CHAT_SUBSCRIBE_PATTERN.matcher(destination);
		if (!matcher.matches()) {
			return;
		}

		Long chatId = Long.valueOf(matcher.group(1));

		Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new AccessDeniedException("Chat not found"));

		if (!chat.getPatientId().equals(user.getId()) && !chat.getDoctorId().equals(user.getId())) {

			throw new AccessDeniedException("You are not a participant of this chat");
		}
	}

}
