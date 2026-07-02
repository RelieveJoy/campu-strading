package com.campus.service.impl;

import com.campus.mapper.SearchMapper;
import com.campus.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final SearchMapper searchMapper;

    @Override
    public List<String> suggestions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return searchMapper.suggestTitles(keyword.trim());
    }
}
