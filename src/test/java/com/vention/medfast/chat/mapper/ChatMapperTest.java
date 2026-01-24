package com.vention.medfast.chat.mapper;

import com.vention.medfast.chat.dto.ChatResponseDto;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.model.Chat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatMapperTest {

	private final ChatMapper mapper = Mappers.getMapper(ChatMapper.class);

	@Test
	void mapsChatToChatResponseDto() {
		Instant now = Instant.now();

		Chat chat = Chat.create(1L, 2L);
		chat.setId(10L);
		chat.setCreatedAt(now);

		ChatResponseDto dto = mapper.toChatResponse(chat);

		assertThat(dto).isNotNull();
		assertThat(dto.id()).isEqualTo(10L);
		assertThat(dto.patientId()).isEqualTo(1L);
		assertThat(dto.doctorId()).isEqualTo(2L);
		assertThat(dto.createdAt()).isEqualTo(now);
	}

	@Test
	void mapsMessageToMessageResponseDto() {
		Instant now = Instant.now();

		Message message = Message.create(5L, 3L, "hello");
		message.setId(99L);
		message.setCreatedAt(now);

		MessageResponseDto dto = mapper.toMessageResponse(message);

		assertThat(dto).isNotNull();
		assertThat(dto.id()).isEqualTo(99L);
		assertThat(dto.chatId()).isEqualTo(5L);
		assertThat(dto.senderId()).isEqualTo(3L);
		assertThat(dto.content()).isEqualTo("hello");
		assertThat(dto.createdAt()).isEqualTo(now);
	}

}
