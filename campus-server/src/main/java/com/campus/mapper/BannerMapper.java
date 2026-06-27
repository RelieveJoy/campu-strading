package com.campus.mapper;

import com.campus.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannerMapper {

    @Select("select * from banner where status = 1 order by sort asc, banner_id desc")
    List<Banner> selectActive();
}
