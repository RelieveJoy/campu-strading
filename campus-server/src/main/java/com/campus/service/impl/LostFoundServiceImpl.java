package com.campus.service.impl;

import com.campus.context.BaseContext;
import com.campus.dto.LostFoundDTO;
import com.campus.dto.LostFoundPageQueryDTO;
import com.campus.entity.LostFound;
import com.campus.exception.BaseException;
import com.campus.mapper.LostFoundMapper;
import com.campus.result.PageResult;
import com.campus.service.LostFoundService;
import com.campus.vo.LostFoundDetailVO;
import com.campus.vo.LostFoundVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LostFoundServiceImpl implements LostFoundService {

    private final LostFoundMapper lostFoundMapper;

    @Override
    @Transactional
    public void publish(LostFoundDTO dto) {
        LostFound entity = LostFound.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .category(dto.getCategory())
                .location(dto.getLocation())
                .contact(dto.getContact())
                .userId(BaseContext.getCurrentId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        lostFoundMapper.insert(entity);
        log.info("用户 {} 发布失物招领: {}", BaseContext.getCurrentId(), dto.getTitle());
    }

    @Override
    public PageResult pageQuery(LostFoundPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<LostFoundVO> list = lostFoundMapper.pageQuery(dto);
        Page<LostFoundVO> p = (Page<LostFoundVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public LostFoundDetailVO getById(Long id) {
        LostFoundDetailVO detail = lostFoundMapper.getDetailById(id);
        if (detail == null) {
            throw new BaseException("失物招领信息不存在");
        }
        return detail;
    }

}
