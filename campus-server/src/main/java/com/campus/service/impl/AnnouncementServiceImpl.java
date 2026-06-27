package com.campus.service.impl;

import com.campus.entity.Announcement;
import com.campus.mapper.AnnouncementMapper;
import com.campus.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> list(int limit) {
        return announcementMapper.selectActive(limit);
    }
}
