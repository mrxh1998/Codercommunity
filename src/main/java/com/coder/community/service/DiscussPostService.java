package com.coder.community.service;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.RedisKeyUtil;
import com.coder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    RedisTemplate redisTemplate;

    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int productId) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, productId);
    }

    public int selectDiscussPostRows(int userId, int productId) {
        return discussPostMapper.selectDiscussPostsRows(userId, productId);
    }

    public int insertDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost selectById(int id) {
        return discussPostMapper.selectById(id);
    }

    public int updateDiscussPostCount(int count, int id) {
        return discussPostMapper.updateCommentCount(count, id);
    }

    public List<DiscussPost> getALlSpecialPosts() {
        return discussPostMapper.selectAllSpecialPosts();
    }

    public List<DiscussPost> findDiscussPostsByIds(Set<Integer> ids, int offset, int limit) {
        return discussPostMapper.selectDiscussPostsByIds(ids, offset, limit);
    }

    public int countDiscussPostsByIds(Set<Integer> ids) {
        return discussPostMapper.countDiscussPostsByIds(ids);
    }

    public void topPost(int postId) {
        DiscussPost discussPost = discussPostMapper.selectById(postId);
        int type = discussPost.getType() == 0 ? 1 : 0;
        int status = discussPostMapper.updatePostType(postId, type);
        if (status != 1) {
            throw new RuntimeException("更新数据库失败");
        }
    }

    public void changeStatus(int postId, int status) {
        int i = discussPostMapper.updatePostStatus(postId, status);
        if (i != 1) {
            throw new RuntimeException("更新数据库失败");
        }
    }

    public void collectPost(int userId, int postId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String userKey = RedisKeyUtil.getUserCollectKey(userId);
                String postKey = RedisKeyUtil.getPostCollectedKey(postId);
                String userCountKey = RedisKeyUtil.getUserCollectCountKey(userId);
                String postCountKey = RedisKeyUtil.getPostCollectedCountKey(postId);
                Boolean isCollect = redisTemplate.opsForSet().isMember(userKey, postId);
                operations.multi();
                if (isCollect) {
                    operations.opsForSet().remove(userKey, postId);
                    operations.opsForSet().remove(postKey, userId);
                    operations.opsForValue().decrement(userCountKey);
                    operations.opsForValue().decrement(postCountKey);
                } else {
                    operations.opsForSet().add(userKey, postId);
                    operations.opsForSet().add(postKey, userId);
                    operations.opsForValue().increment(userCountKey);
                    operations.opsForValue().increment(postCountKey);
                }
                return operations.exec();
            }
        });
    }
    //判断某个人是否收藏
    public int postCollectStatus(int userId,int postId){
        String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userCollectKey, postId)) ?1:0;

    }
}
