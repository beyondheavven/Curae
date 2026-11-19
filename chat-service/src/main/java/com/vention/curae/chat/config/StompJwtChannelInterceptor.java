package com.vention.curae.chat.config;

import com.vention.curae.chat.model.Chat;
import com.vention.curae.chat.repository.ChatRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

	private static final Pattern CHAT_SUBSCRIBE_PATTERN = Pattern.compile("^/topic/chats/(\\d+)$");

	private static final String BEARER_PREFIX = "Bearer ";

	private final ChatRepository chatRepository;

	@Value("${jwt.secret}")
	private String jwtSecret;

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
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			throw new AccessDeniedException("Missing Authorization header");
		}

		String token = authHeader.substring(BEARER_PREFIX.length());
		try {
			Claims claims = Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(token)
				.getPayload();

			Object uid = claims.get("uid");
			String subject = uid != null ? uid.toString() : claims.getSubject();
			if (subject == null || subject.isBlank()) {
				throw new AccessDeniedException("Invalid token");
			}

			List<SimpleGrantedAuthority> authorities = Collections.emptyList();
			Object role = claims.get("role");
			if (role != null) {
				String authority = role.toString().toUpperCase(Locale.ROOT);
				if (!authority.startsWith("ROLE_")) {
					authority = "ROLE_" + authority;
				}
				authorities = List.of(new SimpleGrantedAuthority(authority));
			}

			Authentication authentication = new UsernamePasswordAuthenticationToken(subject, null, authorities);
			accessor.setUser(authentication);
		}
		catch (AccessDeniedException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new AccessDeniedException("Invalid Authorization token");
		}
	}

	private void authorizeSubscription(StompHeaderAccessor accessor) {
		Authentication auth = (Authentication) accessor.getUser();
		if (auth == null || auth.getName() == null) {
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
		Long userId = Long.valueOf(auth.getName());

		Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new AccessDeniedException("Chat not found"));

		if (!chat.getPatientId().equals(userId) && !chat.getDoctorId().equals(userId)) {
			throw new AccessDeniedException("You are not a participant of this chat");
		}
	}

}
