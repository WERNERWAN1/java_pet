package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 原子累加用户积分，避免“先查后改”在并发下丢失更新
     */
    @Update("UPDATE `user` SET score = score + #{points} WHERE id = #{id}")
    int addScore(@Param("id") Long id, @Param("points") int points);

    /**
     * 原子扣减用户积分，最低扣到 0，防止出现负积分
     */
    @Update("UPDATE `user` SET score = GREATEST(score - #{points}, 0) WHERE id = #{id}")
    int deductScore(@Param("id") Long id, @Param("points") int points);
}
