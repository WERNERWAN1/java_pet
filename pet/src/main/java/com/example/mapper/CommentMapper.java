package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getList();

    List<Comment> selectProductCommentList(String productId, String order);

    List<Comment> selectUserCommentList(String userId);

    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLike(@Param("id") String id);
}
