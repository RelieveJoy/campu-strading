package com.campus.task;

import com.campus.mapper.ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class ViewCountSyncTask {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemMapper itemMapper;

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncViewCount() {
        log.info("开始同步商品浏览量...");
        Set<String> keys = redisTemplate.keys("view_count:*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Long itemId = Long.valueOf(key.replace("view_count:", ""));
                Integer count = (Integer) redisTemplate.opsForValue().getAndSet(key, 0);
                if (count != null && count > 0) {
                    itemMapper.updateViewCount(itemId, count);
                    log.info("同步商品[{}]浏览量: {}", itemId, count);
                }
            }
        }
        log.info("浏览量同步完成");
    }
}
