package com.coder.community.service;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.User;
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
import java.util.*;

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

    public void collect(int userId, int postId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
                String postCollectedKey = RedisKeyUtil.getPostCollectedKey(postId);
                operations.multi();
                operations.opsForZSet().add(userCollectKey, postId, System.currentTimeMillis());
                operations.opsForZSet().add(postCollectedKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unCollect(int userId, int postId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
                String postCollectedKey = RedisKeyUtil.getPostCollectedKey(postId);
                operations.multi();
                operations.opsForZSet().remove(userCollectKey, postId);
                operations.opsForZSet().remove(postCollectedKey, userId);
                return operations.exec();
            }
        });
    }

    //查询该用户是否已收藏帖子
    public boolean isCollectPost(int userId, int postId) {
        String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
        return redisTemplate.opsForZSet().score(userCollectKey, postId) != null;
    }

    //得到某个人收藏的贴子数
    public Long getUserCollectCount(int userId) {
        String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
        Long aLong = redisTemplate.opsForZSet().zCard(userCollectKey);
        return aLong == null ? 0 : aLong;
    }

    //查询用户收藏的的帖子
    public List<DiscussPost> findUserCollect(User user, int offset, int limit) {
        String userCollectKey = RedisKeyUtil.getUserCollectKey(user.getId());
        List<DiscussPost> posts = new ArrayList<>();
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(userCollectKey, offset, offset + limit - 1);
        if (set == null) {
            return null;
        }
        for (Integer a : set) {
            DiscussPost discussPost = discussPostMapper.selectById(a);
            posts.add(discussPost);
        }
        return posts;
    }
}
