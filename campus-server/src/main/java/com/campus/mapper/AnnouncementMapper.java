package com.campus.mapper;

import com.campus.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Select("select * from announcement where status = 1 order by create_time desc limit #{limit}")
    List<Announcement> selectActive(int limit);
}
