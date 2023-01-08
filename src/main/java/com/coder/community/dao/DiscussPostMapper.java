package com.coder.community.dao;

import com.coder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int productId);

    int selectDiscussPostsRows(@Param("userId") int userId, int productId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectById(int id);

    int updateCommentCount(int count, int id);

    List<DiscussPost> selectAllSpecialPosts();

    List<DiscussPost> selectDiscussPostsByIds(Set<Integer> ids, int offset, int limit);

    int countDiscussPostsByIds(Set<Integer>ids);
}
