package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    CommentMapper mapper;

    @Override
    public List<Comment> getCommentList() {
        return mapper.getList();
    }

    @Override
    public void createComment(Comment comment) {
        comment.setCommentTime(new Date());
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        mapper.insert(comment);
    }

    @Override
    public void deleteComment(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteCommentByUser(String id, Long userId) {
        Comment comment = mapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的评论");
        }
        mapper.deleteById(id);
    }

    @Override
    public void updateComment(Comment comment) {
        mapper.updateById(comment);
    }

    @Override
    public Comment getCommentDetail(String id) {
        return mapper.selectById(id);
    }

    @Override
    public List<Comment> getProductCommentList(String productId, String order) {
        return mapper.selectProductCommentList(productId, order);
    }

    @Override
    public List<Comment> getUserCommentList(String userId) {
        return mapper.selectUserCommentList(userId);
    }
    
    @Override
    public void likeComment(String commentId) {
        mapper.incrementLike(commentId);
    }
}
