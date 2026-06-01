package com.campus.service.impl;

import com.campus.context.BaseContext;
import com.campus.dto.FavoriteDTO;
import com.campus.entity.Favorite;
import com.campus.exception.ItemBusinessException;
import com.campus.mapper.FavoriteMapper;
import com.campus.service.FavoriteService;
import com.campus.vo.FavoriteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    @Transactional
    public void add(FavoriteDTO favoriteDTO) {
        Long userId = BaseContext.getCurrentId();
        Favorite exist = favoriteMapper.getByUserAndItem(userId, favoriteDTO.getItemId());
        if (exist != null) {
            throw new ItemBusinessException("已收藏该商品，不可重复收藏");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemId(favoriteDTO.getItemId());
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void delete(Long favoriteId) {
        Favorite favorite = favoriteMapper.getById(favoriteId);
        if (favorite == null) {
            throw new ItemBusinessException("收藏记录不存在");
        }
        if (!favorite.getUserId().equals(BaseContext.getCurrentId())) {
            throw new ItemBusinessException("只能取消自己的收藏");
        }
        favoriteMapper.delete(favoriteId);
    }

    @Override
    public List<FavoriteVO> list() {
        return favoriteMapper.pageQueryByUserId(BaseContext.getCurrentId());
    }

    @Override
    public boolean check(Long itemId) {
        Favorite favorite = favoriteMapper.getByUserAndItem(BaseContext.getCurrentId(), itemId);
        return favorite != null;
    }
}
