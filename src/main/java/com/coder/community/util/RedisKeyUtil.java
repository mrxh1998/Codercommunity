package com.coder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKER = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_USER_COLLECT = "userCollect";
    private static final String PREFIX_POST_COLLECTED = "postCollected";
    private static final String PREFIX_USER_COLLECT_COUNT = "userCollectCount";
    private static final String PREFIX_POST_COLLECTED_COUNT = "userCollectCount";
    //用户收藏的帖子计数
    public static String getUserCollectCountKey(int userId){
        return PREFIX_USER_COLLECT_COUNT + SPLIT + userId;
    }
    //收藏帖子的人数计数
    public static String getPostCollectedCountKey(int postId){
        return PREFIX_POST_COLLECTED_COUNT + SPLIT + postId;
    }
    //收藏某个帖子的用户
    public static String getPostCollectedKey(int postId){
        return PREFIX_POST_COLLECTED + SPLIT + postId;
    }
    //某个用户收藏的帖子
    public static String getUserCollectKey(int userId){
        return PREFIX_USER_COLLECT + SPLIT + userId;
    }
    //某个实体的赞
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    //某个用户获得的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
    //某个用户关注的实体
    //followee:userId:entityType->zset(entityId,now)
    public static String getFollowee(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个用户拥有的粉丝
    //follower:entityType:entityId->zset(userId,now)
    public static String getFollower(int entityType,int entityId){
        return PREFIX_FOLLOWER +SPLIT + entityType + SPLIT + entityId;
    }

    //验证码key

    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //登陆凭证key
    public static  String getTicketKey(String ticket){
        return PREFIX_TICKER+SPLIT+ticket;
    }

    //用户key
    public static  String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
