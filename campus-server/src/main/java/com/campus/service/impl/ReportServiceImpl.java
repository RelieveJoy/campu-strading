package com.campus.service.impl;

import com.campus.context.BaseContext;
import com.campus.dto.ReportDTO;
import com.campus.entity.Report;
import com.campus.mapper.ReportMapper;
import com.campus.result.PageResult;
import com.campus.service.ReportService;
import com.campus.vo.ReportVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public void submit(ReportDTO dto) {
        Report report = Report.builder()
                .reporterId(BaseContext.getCurrentId())
                .targetType(dto.getTargetType())
                .targetId(dto.getTargetId())
                .reason(dto.getReason())
                .description(dto.getDescription())
                .status(Report.STATUS_PENDING)
                .build();
        reportMapper.insert(report);
        log.info("用户 {} 举报 {}/{}: {}", BaseContext.getCurrentId(), dto.getTargetType(), dto.getTargetId(), dto.getReason());
    }

    @Override
    public PageResult pageQuery(Integer status, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<ReportVO> list = reportMapper.pageQuery(status);
        Page<ReportVO> p = (Page<ReportVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public void markResolved(Long reportId) {
        reportMapper.markResolved(reportId);
        log.info("举报 {} 已处理", reportId);
    }
}
