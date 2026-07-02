package com.campus.mapper;

import com.campus.entity.Report;
import com.campus.vo.ReportVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReportMapper {

    @Insert("insert into report(reporter_id, target_type, target_id, reason, description, status) " +
            "values (#{reporterId}, #{targetType}, #{targetId}, #{reason}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "reportId")
    void insert(Report report);

    List<ReportVO> pageQuery(Integer status);

    @Update("update report set status = 2 where report_id = #{reportId}")
    int markResolved(Long reportId);
}
