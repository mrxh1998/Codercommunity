package com.coder.community.dao;

import com.coder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int productId);
    int selectDiscussPostsRows(@Param("userId") int userId,int productId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectById(int id);
    int updateCommentCount(int count,int id);
    List<DiscussPost> selectAllSpecialPosts();
}
