package com.campus.mapper;

import com.campus.annotation.AutoFill;
import com.campus.dto.OrdersPageQueryDTO;
import com.campus.entity.Orders;
import com.campus.enumeration.OperationType;
import com.campus.vo.OrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("insert into orders(item_id, buyer_id, seller_id, amount, status, create_time, update_time, create_user, update_user) " +
            "values (#{itemId}, #{buyerId}, #{sellerId}, #{amount}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Orders order);

    @AutoFill(OperationType.UPDATE)
    void update(Orders order);

    @Select("select * from orders where order_id = #{orderId}")
    Orders getById(Long orderId);

    List<OrderVO> pageQuery(OrdersPageQueryDTO dto);

    OrderVO getDetailById(Long orderId);
}
