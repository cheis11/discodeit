package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    public List<Message> findAllByChannelId(UUID channelId);

    public Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

    public List<Message> findAllByChannelIdAndAuthorId(UUID channelId, UUID userId);
}
