package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getList();

    List<Order> getUserOrderList(String userId, String status);

    @Update("UPDATE `order` SET order_number = CONCAT('D00', id) WHERE id = #{id}")
    int fillOrderNumber(@Param("id") Long id);

    /**
     * 标记订单积分已发放：仅当此前未发放时才更新，返回 1 表示本次抢到发放资格，避免重复入账
     */
    @Update("UPDATE `order` SET award_score = #{points} WHERE id = #{id} AND award_score = 0")
    int markScoreAwarded(@Param("id") Long id, @Param("points") int points);

    /**
     * 清除订单积分发放标记（订单取消时回调），返回 1 表示此前确实已发放
     */
    @Update("UPDATE `order` SET award_score = 0 WHERE id = #{id} AND award_score > 0")
    int clearScoreAwarded(@Param("id") Long id);
}
