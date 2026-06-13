package com.campus.runner;

import com.campus.service.ItemService;
import com.campus.vo.ItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动后预热首页商品缓存，避免第一个访客等待查库。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupRunner {

    private final ItemService itemService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupHomeCache() {
        log.info("开始预热首页商品缓存...");
        try {
            List<ItemVO> items = itemService.getHomeItems();
            log.info("首页缓存预热完成，共 {} 条商品", items.size());
        } catch (Exception e) {
            log.warn("缓存预热失败（不影响正常运行）: {}", e.getMessage());
        }
    }
}
