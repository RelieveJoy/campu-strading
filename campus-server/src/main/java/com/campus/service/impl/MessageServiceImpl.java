package com.campus.service.impl;

import com.campus.entity.Message;
import com.campus.mapper.MessageMapper;
import com.campus.service.MessageService;
import com.campus.vo.ConversationVO;
import com.campus.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public MessageVO send(Long senderId, Long itemId, String sourceType, Long receiverId, String content) {
        Message message = Message.builder()
                .itemId(itemId)
                .sourceType(sourceType != null ? sourceType : "item")
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);

        return MessageVO.builder()
                .messageId(message.getMessageId())
                .itemId(itemId)
                .sourceType(message.getSourceType())
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .isRead(0)
                .createTime(message.getCreateTime())
                .build();
    }

    @Override
    public List<MessageVO> getHistory(Long itemId, String sourceType, Long userId, Long otherId) {
        return messageMapper.listByItemId(itemId, sourceType != null ? sourceType : "item", userId, otherId);
    }

    @Override
    public void markRead(Long userId, Long itemId, String sourceType) {
        messageMapper.markRead(userId, itemId, sourceType != null ? sourceType : "item");
    }

    @Override
    public List<ConversationVO> listConversations(Long userId) {
        return messageMapper.listConversations(userId);
    }
}
