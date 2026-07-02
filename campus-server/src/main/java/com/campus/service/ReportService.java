package com.campus.service;

import com.campus.dto.ReportDTO;
import com.campus.result.PageResult;

public interface ReportService {

    void submit(ReportDTO dto);

    PageResult pageQuery(Integer status, int page, int pageSize);

    void markResolved(Long reportId);
}
