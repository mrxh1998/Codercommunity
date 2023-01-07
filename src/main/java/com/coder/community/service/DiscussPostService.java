package com.coder.community.service;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;
    public List<DiscussPost>  selectDiscussPosts(int userId,int offset,int limit,int productId){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit,productId);
    }
    public int selectDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostsRows(userId);
    }

    public int insertDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }
    public DiscussPost selectById(int id){
        return discussPostMapper.selectById(id);
    }
    public int updateDiscussPostCount(int count,int id){
        return discussPostMapper.updateCommentCount(count,id);
    }
    public List<DiscussPost> getALlSpecialPosts(){
        return discussPostMapper.selectAllSpecialPosts();
    }
}
