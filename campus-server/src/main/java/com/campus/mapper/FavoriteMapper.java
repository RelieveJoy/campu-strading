package com.campus.mapper;

import com.campus.annotation.AutoFill;
import com.campus.entity.Favorite;
import com.campus.enumeration.OperationType;
import com.campus.vo.FavoriteVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("insert into favorite(user_id, item_id, create_time) values(#{userId}, #{itemId}, #{createTime})")
    @AutoFill(OperationType.INSERT)
    void insert(Favorite favorite);

    @Delete("delete from favorite where favorite_id = #{favoriteId}")
    void delete(Long favoriteId);

    @Select("select * from favorite where favorite_id = #{favoriteId}")
    Favorite getById(Long favoriteId);

    @Select("select * from favorite where user_id = #{userId} and item_id = #{itemId}")
    Favorite getByUserAndItem(Long userId, Long itemId);

    List<FavoriteVO> pageQueryByUserId(Long userId);
}
