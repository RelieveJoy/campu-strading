package com.campus.mapper;

import com.campus.dto.LostFoundPageQueryDTO;
import com.campus.entity.LostFound;
import com.campus.vo.LostFoundDetailVO;
import com.campus.vo.LostFoundVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LostFoundMapper {

    @Insert("insert into lost_found(title, description, image_url, category, location, contact, user_id, create_time, update_time) " +
            "values (#{title}, #{description}, #{imageUrl}, #{category}, #{location}, #{contact}, #{userId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "lostFoundId")
    void insert(LostFound entity);

    @Select("select * from lost_found where lost_found_id = #{id}")
    LostFound getById(Long id);

    List<LostFoundVO> pageQuery(LostFoundPageQueryDTO dto);

    LostFoundDetailVO getDetailById(Long id);
}
