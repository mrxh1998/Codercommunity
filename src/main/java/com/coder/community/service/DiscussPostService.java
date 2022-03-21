package com.coder.community.service;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    public List<DiscussPost>  selectDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }
    public int selectDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostsRows(userId);
    }
}
