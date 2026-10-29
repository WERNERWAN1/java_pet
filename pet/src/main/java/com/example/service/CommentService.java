package com.example.service;


import com.example.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentList();
    void createComment(Comment comment);
    void deleteComment(String id);
    void updateComment(Comment comment);
    Comment getCommentDetail(String id);

    List<Comment> getProductCommentList(String productId, String order);

    List<Comment> getUserCommentList(String userId);
    
    void deleteCommentByUser(String id, Long userId);

    // 点赞
    void likeComment(String commentId);
}
