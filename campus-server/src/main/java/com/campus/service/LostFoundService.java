package com.campus.service;

import com.campus.dto.LostFoundDTO;
import com.campus.dto.LostFoundPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.vo.LostFoundDetailVO;

public interface LostFoundService {

    void publish(LostFoundDTO dto);

    PageResult pageQuery(LostFoundPageQueryDTO dto);

    LostFoundDetailVO getById(Long id);
}
