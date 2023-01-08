package com.coder.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0; //激活成功
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FALURE = 2;
    int DEFAULT_EXPIRED_SECONDS = 3600*12; //默认状态超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100; //记住我的事件
    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST=1;
    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT=2;
    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER=3;
    /**
     * 事件主题：评论
     */
    String TOPIC_COMMENT="comment";
    /**
     * 事件：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 事件：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;
    /**
     * 事件：置顶
     */
    String TOPIC_TOP_POST = "topPost";
    /**
     * 事件：加精
     */
    String TOPIC_STATUS_POST = "status";
}
