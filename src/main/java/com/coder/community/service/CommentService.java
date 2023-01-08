package com.coder.community.service;

import com.coder.community.dao.CommentMapper;
import com.coder.community.entity.Comment;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Set;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    DiscussPostService discussPostService;
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
    public List<Comment> findCommentByUserId(int userId){
        return commentMapper.selectCommentByUserId(userId);
    }
    public int countCommentByUserId(int userId){
        return commentMapper.countCommentByUserId(userId);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int i = commentMapper.insertComment(comment);
        if(comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateDiscussPostCount(count,comment.getEntityId());
        }
        return i;
    }
    public List<Comment> selectCommentByIds(Set<Integer> ids){
        return commentMapper.selectCommentsByIds(ids);
    }
}
