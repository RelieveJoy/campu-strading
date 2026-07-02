package com.campus.service;

import com.campus.context.BaseContext;
import com.campus.dto.ReportDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.mapper.ReportMapper;
import com.campus.mapper.UserMapper;
import com.campus.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/schema.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReportServiceTest {

    @Autowired private ReportService reportService;
    @Autowired private ReportMapper reportMapper;
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;

    private Long reporterId;

    @BeforeEach
    void setUp() {
        userService.register(buildUserDTO("reporter01", "举报人"));
        reporterId = userMapper.getByStudentId("reporter01").getUserId();
        BaseContext.setCurrentId(reporterId);
    }

    @Test
    @DisplayName("提交举报成功")
    void submitSuccess() {
        ReportDTO dto = new ReportDTO();
        dto.setTargetType("item");
        dto.setTargetId(1L);
        dto.setReason("虚假信息");
        dto.setDescription("商品描述与实际不符");
        assertDoesNotThrow(() -> reportService.submit(dto));
    }

    @Test
    @DisplayName("分页查询举报列表")
    void pageQueryReturnsList() {
        ReportDTO dto = new ReportDTO();
        dto.setTargetType("item");
        dto.setTargetId(1L);
        dto.setReason("虚假信息");
        reportService.submit(dto);

        ReportDTO dto2 = new ReportDTO();
        dto2.setTargetType("user");
        dto2.setTargetId(2L);
        dto2.setReason("骚扰行为");
        reportService.submit(dto2);

        PageResult result = reportService.pageQuery(null, 1, 10);
        assertTrue(result.getTotal() >= 2);
        assertEquals(2, result.getRecords().size());
    }

    @Test
    @DisplayName("按状态筛选待处理的举报")
    void pageQueryFilterByStatus() {
        ReportDTO dto = new ReportDTO();
        dto.setTargetType("item");
        dto.setTargetId(1L);
        dto.setReason("虚假信息");
        reportService.submit(dto);

        PageResult result = reportService.pageQuery(1, 1, 10);
        assertTrue(result.getTotal() >= 1);
    }

    @Test
    @DisplayName("标记举报已处理")
    void markResolvedSuccess() {
        ReportDTO dto = new ReportDTO();
        dto.setTargetType("item");
        dto.setTargetId(1L);
        dto.setReason("虚假信息");
        reportService.submit(dto);

        // 查 ID
        var reports = reportMapper.pageQuery(null);
        assertFalse(reports.isEmpty());
        Long reportId = reports.get(0).getReportId();

        reportService.markResolved(reportId);

        var after = reportMapper.pageQuery(2);
        assertFalse(after.isEmpty());
        assertEquals(2, after.get(0).getStatus().intValue());
    }

    private UserRegisterDTO buildUserDTO(String sid, String name) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setStudentId(sid);
        dto.setUsername(name);
        dto.setPassword("123456");
        dto.setPhone("13800001111");
        return dto;
    }
}
