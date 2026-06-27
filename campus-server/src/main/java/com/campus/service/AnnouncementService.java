package com.campus.service;

import com.campus.entity.Announcement;

import java.util.List;

public interface AnnouncementService {

    List<Announcement> list(int limit);
}
