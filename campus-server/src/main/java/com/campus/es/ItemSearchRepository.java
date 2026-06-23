package com.campus.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, Long> {

    /** 搜索标题和描述中匹配关键词的在售商品 */
    List<ItemDocument> findByTitleOrDescriptionAndStatus(String title, String description, Integer status);
}
