package com.campus.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchMapper {

    @Select("select distinct title from item where status = 1 and title like concat('%', #{keyword}, '%') limit 5")
    List<String> suggestTitles(@Param("keyword") String keyword);
}
