package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface MessageMapper {

    @Mapping(target = "attachments", qualifiedByName = "binaryToDtoList")
    MessageDto messageToMessageResponseDto(Message message);
}
