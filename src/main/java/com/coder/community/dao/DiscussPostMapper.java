package com.coder.community.dao;

import com.coder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int productId, String like);

    int selectDiscussPostsRows(@Param("userId") int userId, int productId, String like);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectById(int id);

    int updateCommentCount(int count, int id);

    List<DiscussPost> selectAllSpecialPosts();

    List<DiscussPost> selectDiscussPostsByIds(Set<Integer> ids, int offset, int limit);

    int countDiscussPostsByIds(Set<Integer> ids);

    int updatePostType(int id, int type);

    int updatePostStatus(int id, int status);
}
