package com.campus.service;

import com.campus.dto.ItemDTO;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.result.PageResult;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;

import java.util.List;

public interface ItemService {

    void add(ItemDTO itemDTO);

    void update(Long itemId, ItemDTO itemDTO);

    void delete(Long itemId);

    void relist(Long itemId);

    PageResult pageQuery(ItemPageQueryDTO dto);

    ItemDetailVO getById(Long itemId);

    List<ItemVO> getByUserId(Long userId);
}
