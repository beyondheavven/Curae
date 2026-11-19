package com.vention.curae.chat.mapper;

import com.vention.curae.chat.dto.ChatResponseDto;
import com.vention.curae.chat.message.dto.MessageResponseDto;
import com.vention.curae.chat.message.model.Message;
import com.vention.curae.chat.model.Chat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

	ChatResponseDto toChatResponse(Chat chat);

	MessageResponseDto toMessageResponse(Message message);

}
