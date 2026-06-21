package com.campus.mapper;

import com.campus.entity.Notification;
import com.campus.vo.NotificationVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("insert into notification(user_id, initiator_id, initiator_name, type, order_id, item_title, content, create_time) " +
            "values(#{userId}, #{initiatorId}, #{initiatorName}, #{type}, #{orderId}, #{itemTitle}, #{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "notificationId")
    void insert(Notification notification);

    @Select("select notification_id, initiator_id, initiator_name, type, order_id, item_title, content, is_read, create_time " +
            "from notification where user_id = #{userId} order by create_time desc")
    List<NotificationVO> listByUserId(Long userId);

    @Update("update notification set is_read = 1 where user_id = #{userId} and is_read = 0")
    void markAllRead(Long userId);
}
