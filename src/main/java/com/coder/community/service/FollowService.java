package com.coder.community.service;

import com.coder.community.dao.UserMapper;
import com.coder.community.entity.User;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFollowee(userId,entityType);
                String followerKey = RedisKeyUtil.getFollower(entityType, entityId);
                operations.multi();
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unFollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFollowee(userId,entityType);
                String followerKey = RedisKeyUtil.getFollower(entityType, entityId);
                operations.multi();
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);
                return operations.exec();
            }
        });
    }
    //查询该用户是否已关注该实体
    public boolean isFollowEntity(int userId,int entityType,int entityId){
        String followee = RedisKeyUtil.getFollowee(userId, entityType);
        return redisTemplate.opsForZSet().score(followee, entityId) != null;
    }

    //查询用户关注的实体数量
    public long findFolloweeCount(int userId,int entityType){
        String followee = RedisKeyUtil.getFollowee(userId, entityType);
        Long aLong = redisTemplate.opsForZSet().zCard(followee);
        return aLong==null?0:aLong;
    }
    //查询实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId){
        String follower = RedisKeyUtil.getFollower(entityType, entityId);
        Long aLong = redisTemplate.opsForZSet().zCard(follower);
        if(aLong == null){
            return 0;
        }
        return aLong;
    }
    //查询用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFollowee(userId, ENTITY_TYPE_USER);
        List<Map<String,Object>> followees = new ArrayList<>();
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit-1);
        if(set == null){
            return null;
        }
        for(Integer a:set){
            User user = userMapper.selectById(a);
            Double score = redisTemplate.opsForZSet().score(followeeKey, a);
            Map<String,Object> map = new HashMap<>();
            map.put("user",user);
            map.put("followTime",new Date(score.longValue()));
            followees.add(map);
        }
        return followees;
    }
    //查询某个用户粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollower(ENTITY_TYPE_USER,userId);
        List<Map<String,Object>> followers = new ArrayList<>();
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit-1);
        if(set == null){
            return null;
        }
        for(Integer a:set){
            User user = userMapper.selectById(a);
            Double score = redisTemplate.opsForZSet().score(followerKey, a);
            Map<String,Object> map = new HashMap<>();
            map.put("user",user);
            map.put("followTime",new Date(score.longValue()));
            followers.add(map);
        }
        return followers;
    }
}
