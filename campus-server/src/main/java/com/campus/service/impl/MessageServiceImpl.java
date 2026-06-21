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
    public MessageVO send(Long senderId, Long itemId, Long receiverId, String content) {
        Message message = Message.builder()
                .itemId(itemId)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);

        return MessageVO.builder()
                .messageId(message.getMessageId())
                .itemId(itemId)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .isRead(0)
                .createTime(message.getCreateTime())
                .build();
    }

    @Override
    public List<MessageVO> getHistory(Long itemId, Long userId, Long otherId) {
        return messageMapper.listByItemId(itemId, userId, otherId);
    }

    @Override
    public void markRead(Long userId, Long itemId) {
        messageMapper.markRead(userId, itemId);
    }

    @Override
    public List<ConversationVO> listConversations(Long userId) {
        return messageMapper.listConversations(userId);
    }
}
