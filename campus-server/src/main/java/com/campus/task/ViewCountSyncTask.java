package com.campus.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 浏览量同步定时任务
 * 每10次访问异步回写MySQL
 */
@Component
@Slf4j
public class ViewCountSyncTask {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每5分钟执行一次浏览量同步
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncViewCount() {
        log.info("开始同步商品浏览量...");
        // TODO: 扫描 Redis 中 view_count:* 的 key，批量回写 MySQL
        Set<String> keys = redisTemplate.keys("view_count:*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Long itemId = Long.valueOf(key.replace("view_count:", ""));
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                if (count != null) {
                    // TODO: 调用 itemMapper.updateViewCount(itemId, count)
                    log.info("同步商品[{}]浏览量: {}", itemId, count);
                }
            }
        }
    }
}
