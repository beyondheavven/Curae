package com.vention.medfast.chat.mapper;

import com.vention.medfast.chat.dto.ChatResponseDto;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.model.Message;
import com.vention.medfast.chat.model.Chat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

	ChatResponseDto toChatResponse(Chat chat);

	MessageResponseDto toMessageResponse(Message message);

}
