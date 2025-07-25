package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BasicBinaryContentService basicBinaryContentService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public MessageDto createMessage(MessageCreateServiceRequest request) {
        User author =
                userRepository
                        .findById(request.authorId())
                        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        Channel channel =
                channelRepository
                        .findById(request.channelId())
                        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        Message message = new Message(request.content(), author, channel);

        messageRepository.save(message);

        List<MultipartFile> imageFiles = request.image();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<BinaryContent> attachments =
                    imageFiles.stream()
                            .filter(multipartFile -> multipartFile != null && !multipartFile.isEmpty())
                            .map(
                                    file -> {
                                        BinaryContentDto created =
                                                basicBinaryContentService.createBinaryContent(
                                                        new BinaryContentCreateServiceRequest(message.getId(), null, file));
                                        byte[] decodedBytes = null;
                                        if (created.bytes() != null && !created.bytes().isEmpty()) {
                                            decodedBytes = Base64.getDecoder().decode(created.bytes());
                                        }
                                        binaryContentStorage.put(created.id(), decodedBytes);

                                        return binaryContentMapper.binaryContentDtoToBinaryContent(
                                                created);
                                    })
                            .collect(Collectors.toList());

            message.setAttachments(attachments);
        }

        messageRepository.save(message);

        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public MessageDto updateMessage(UUID messageId, MessageUpdateServiceRequest request) {
        Message message =
                messageRepository
                        .findById(messageId)
                        .orElseThrow(() -> new MessageNotFoundException("해당 메시지를 찾을 수 없습니다."));

        if (request.newContent() != null && !request.newContent().equals(message.getContent())) {
            message.setContent(request.newContent());
        }

        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message =
                messageRepository
                        .findById(messageId)
                        .orElseThrow(() -> new MessageNotFoundException("메시지를 찾을 수 없습니다."));

        binaryContentRepository.deleteAll(message.getAttachments());
        message.getAttachments().clear();
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(messageMapper::messageToMessageResponseDto)
                .collect(Collectors.toList());
    }
}
