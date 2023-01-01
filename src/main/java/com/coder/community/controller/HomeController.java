package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import org.apache.commons.csv.CSVFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    LikeService likeService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.selectDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!= null){
            for(DiscussPost post: list){
                int userId = post.getUserId();
                User user = userService.selectById(userId);
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",user);
                long likeCount = likeService.entityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
