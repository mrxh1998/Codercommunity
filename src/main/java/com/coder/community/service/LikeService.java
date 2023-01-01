package com.coder.community.service;

import com.coder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean isMember = redisTemplate.opsForSet().isMember(entityKey, userId);
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityKey,userId);
                    operations.opsForValue().decrement(userKey);
                }
                else{
                    operations.opsForSet().add(entityKey,userId);
                    operations.opsForValue().increment(userKey);
                }
                return operations.exec();
            }
        });
    }
    //统计实体点赞数量
    public long entityLikeCount(int entityType,int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityKey);
    }
    //判断某个人是否点赞
    public int entityLikeStatus(int userId,int entityType,int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityKey, userId)?1:0;

    }
    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer num = (Integer) redisTemplate.opsForValue().get(userKey);
        if(num == null){
            return 0;
        }
        return num.intValue();
    }
}
