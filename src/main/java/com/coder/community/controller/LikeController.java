package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import com.coder.community.entity.Event;
import com.coder.community.entity.User;
import com.coder.community.event.EventProducer;
import com.coder.community.service.LikeService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import com.google.code.kaptcha.Producer;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController  implements CommunityConstant {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer producer;
    @RequestMapping(path="/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        long likeCount = likeService.entityLikeCount(entityType,entityId);
        int status = likeService.entityLikeStatus(user.getId(), entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",status);
        //触发点赞事件
        if(status == 1){
            Event event = new Event();
            event.setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setMap("postId",postId);
            producer.fireEvent(event);
        }
        return CommunityUtil.getJSONString(0,null,map);
    }
}
