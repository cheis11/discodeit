package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@NoArgsConstructor
@Getter
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_id")
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(String messageContents, User user, Channel channel) {
        super();
        this.content = messageContents;
        this.channel = channel;
        this.author = user;
    }

    public void setAttachment(BinaryContent attachment) {
        this.attachments.add(attachment);
    }

    @Override
    public String toString() {

        return "Message{"
                + "\n"
                + "messageContents="
                + content
                + "\n"
                + "userId="
                + author.getId()
                + "\n"
                + "channelId="
                + channel.getId()
                + "\n"
                + "createdAt="
                + getCreatedAt()
                + "\n"
                + "updatedAt="
                + getUpdatedAt()
                + "\n"
                + '}'
                + "\n";
    }
}
