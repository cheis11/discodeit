package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MessageDto> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest dto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageCreateServiceRequest createRequest =
                new MessageCreateServiceRequest(
                        dto.content(), dto.channelId(), dto.authorId(), attachments);
        MessageDto created = messageService.createMessage(createRequest);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageUpdateServiceRequest messageUpdateServiceRequest =
                new MessageUpdateServiceRequest(request.newContent(), attachments);

        MessageDto updated = messageService.updateMessage(id, messageUpdateServiceRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessagesByChannel(@RequestParam UUID channelId) {
        List<MessageDto> messageDtos = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageDtos);
    }
}
