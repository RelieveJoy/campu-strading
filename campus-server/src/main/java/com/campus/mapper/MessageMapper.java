package com.campus.mapper;

import com.campus.entity.Message;
import com.campus.vo.ConversationVO;
import com.campus.vo.MessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("insert into message(item_id, sender_id, receiver_id, content, create_time) " +
            "values(#{itemId}, #{senderId}, #{receiverId}, #{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    void insert(Message message);

    /** 查某个商品下两个用户之间的聊天记录（XML：listByItemId） */
    List<MessageVO> listByItemId(Long itemId, Long userId, Long otherId);

    /** 将对方发来的消息标记为已读 */
    @Update("update message set is_read = 1 " +
            "where item_id = #{itemId} and receiver_id = #{userId} and is_read = 0")
    void markRead(Long userId, Long itemId);

    /** 查当前用户参与的所有对话列表（XML：listConversations） */
    List<ConversationVO> listConversations(Long userId);
}
