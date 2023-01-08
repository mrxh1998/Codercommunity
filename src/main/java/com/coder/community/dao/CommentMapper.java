package com.coder.community.dao;

import com.coder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface CommentMapper {
    List<Comment> selectByEntity(int entityType,int entityId,int offset,int limit);
    int selectCountByEntity(int entityType,int entityId);
    int insertComment(Comment comment);
    Comment selectCommentById(int id);
    List<Comment> selectCommentByUserId(int userId);
    int countCommentByUserId(int userId);
    List<Comment> selectCommentsByIds(Set<Integer> ids);
}
