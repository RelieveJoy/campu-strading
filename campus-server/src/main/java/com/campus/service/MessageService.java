package com.campus.service;

import com.campus.vo.ConversationVO;
import com.campus.vo.MessageVO;

import java.util.List;

public interface MessageService {

    /** 发送消息 */
    MessageVO send(Long senderId, Long itemId, Long receiverId, String content);

    /** 查某个商品下当前用户和对方的聊天记录 */
    List<MessageVO> getHistory(Long itemId, Long userId, Long otherId);

    /** 标记已读 */
    void markRead(Long userId, Long itemId);

    /** 查当前用户参与的所有对话列表 */
    List<ConversationVO> listConversations(Long userId);
}
