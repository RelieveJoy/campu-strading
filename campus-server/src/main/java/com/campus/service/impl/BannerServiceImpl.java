package com.campus.service.impl;

import com.campus.entity.Banner;
import com.campus.mapper.BannerMapper;
import com.campus.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<Banner> list() {
        return bannerMapper.selectActive();
    }
}
