package com.campus.mapper;

import com.campus.annotation.AutoFill;
import com.campus.dto.ItemPageQueryDTO;
import com.campus.entity.Item;
import com.campus.enumeration.OperationType;
import com.campus.vo.ItemDetailVO;
import com.campus.vo.ItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {

    @Insert("insert into item(seller_id, title, description, price, original_price, category_id, status, item_condition, image_url, view_count, create_time, update_time, create_user, update_user) " +
            "values " +
            "(#{sellerId}, #{title}, #{description}, #{price}, #{originalPrice}, #{categoryId}, #{status}, #{itemCondition}, #{imageUrl}, #{viewCount}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Item item);

    @AutoFill(OperationType.UPDATE)
    void update(Item item);

    List<ItemVO> pageQuery(ItemPageQueryDTO dto);

    /** 用 ES 返回的 ID 列表 + 其他筛选条件查 */
    List<ItemVO> pageQueryByIds(@Param("ids") List<Long> ids, @Param("dto") ItemPageQueryDTO dto);

    @Select("select * from item where item_id = #{itemId}")
    Item getById(Long itemId);

    ItemDetailVO getDetailById(Long itemId);

    List<ItemVO> getByUserId(Long userId);

    @Update("update item set view_count = #{count} where item_id = #{itemId}")
    void updateViewCount(Long itemId, Integer count);

    /**
     * 原子更新商品状态为已售出（CAS：仅当 status=1 时更新）。
     * @return 受影响行数，0 表示已被其他事务抢先修改
     */
    @Update("update item set status = 2 where item_id = #{itemId} and status = 1")
    int updateStatusToSold(Long itemId);
}
