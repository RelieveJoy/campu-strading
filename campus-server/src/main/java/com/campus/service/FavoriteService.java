package com.campus.service;

import com.campus.dto.FavoriteDTO;
import com.campus.vo.FavoriteVO;

import java.util.List;

public interface FavoriteService {

    void add(FavoriteDTO favoriteDTO);

    void delete(Long favoriteId);

    List<FavoriteVO> list();

    boolean check(Long itemId);
}
