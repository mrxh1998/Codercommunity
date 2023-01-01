package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import com.coder.community.entity.Event;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.event.EventProducer;
import com.coder.community.service.FollowService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements  CommunityConstant{
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer producer;

    @RequestMapping(path="/follow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);
        //触发关注事件
        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        producer.fireEvent(event);
        return CommunityUtil.getJSONString(0,"已关注");
    }
    @RequestMapping(path="/unFollow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unFollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    //返回关注人列表界面
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFolloweesPage(@PathVariable("userId")int userId, Model model, Page page){
        User user = userService.selectById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        page.setLimit(10);
        page.setPath("/followees/"+userId);
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList !=null){
            for(Map map:userList){
                User nowUser = hostHolder.getUser();
                User targetUser = (User)map.get("user");
                boolean status = false;
                if(nowUser != null){
                    status = followService.isFollowEntity(nowUser.getId(), ENTITY_TYPE_USER, targetUser.getId());
                }
                map.put("status",status);
            }
        }
        model.addAttribute("users",userList);
        return "/site/followee";
    }
    //返回粉丝列表界面
    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowersPage(@PathVariable("userId")int userId, Model model, Page page){
        User user = userService.selectById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        page.setLimit(10);
        page.setPath("/followers/"+userId);
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList !=null){
            for(Map map:userList){
                User nowUser = hostHolder.getUser();
                User targetUser = (User)map.get("user");
                boolean status = false;
                if(nowUser != null){
                    status = followService.isFollowEntity(nowUser.getId(), ENTITY_TYPE_USER, targetUser.getId());
                }
                map.put("status",status);
            }
        }
        model.addAttribute("users",userList);
        return "/site/follower";
    }
}
