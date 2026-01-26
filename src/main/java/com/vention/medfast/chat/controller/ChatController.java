package com.vention.medfast.chat.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.chat.dto.ChatCreateRequestDto;
import com.vention.medfast.chat.dto.ChatResponseDto;
import com.vention.medfast.chat.mapper.ChatMapper;
import com.vention.medfast.chat.message.dto.MessageResponseDto;
import com.vention.medfast.chat.message.dto.MessageSendRequestDto;
import com.vention.medfast.chat.model.Chat;
import com.vention.medfast.chat.service.ChatService;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CHAT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Chats", description = "Patient–Doctor chat endpoints")
public class ChatController {

	private final ChatService chatService;

	private final ChatMapper chatMapper;

	@Operation(summary = "Create chat with doctor",
			description = "Creates a 1-to-1 chat between patient and selected doctor")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Chat created",
					content = @Content(schema = @Schema(implementation = ChatResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Business validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	@PreAuthorize("hasRole('PATIENT')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<ChatResponseDto> createChat(@AuthenticationPrincipal CustomUserDetails user,
			@Valid @RequestBody ChatCreateRequestDto dto) {
		Chat chat = chatService.createChat(user.getId(), dto.doctorId());
		return ResponseWrapper.withStatus(HttpStatus.CREATED, chatMapper.toChatResponse(chat));
	}

	@Operation(summary = "Get my chats", description = "Returns list of chats for authenticated user")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Chats returned"),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping
	public ResponseWrapper<List<ChatResponseDto>> getMyChats(@AuthenticationPrincipal CustomUserDetails user) {
		List<ChatResponseDto> chats = chatService.getUserChats(user.getId())
			.stream()
			.map(chatMapper::toChatResponse)
			.toList();

		return ResponseWrapper.ok(chats);
	}

	@Operation(summary = "Get chat messages", description = "Returns paginated messages of selected chat")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Messages returned"),
			@ApiResponse(responseCode = "403", description = "User is not chat participant",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Chat not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{chatId}/messages")
	public ResponseWrapper<Page<MessageResponseDto>> getMessages(@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable Long chatId, @PageableDefault(size = 20) Pageable pageable) {
		Page<MessageResponseDto> page = chatService.getChatMessages(chatId, user.getId(), pageable)
			.map(chatMapper::toMessageResponse);

		return ResponseWrapper.ok(page);
	}

	@Operation(summary = "Send message to chat", description = "Sends a text message to the selected chat")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Message sent",
					content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/{chatId}/messages")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<MessageResponseDto> sendMessage(@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable Long chatId, @Valid @RequestBody MessageSendRequestDto dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED,
				chatMapper.toMessageResponse(chatService.createMessage(chatId, user.getId(), dto.content())));
	}

}
