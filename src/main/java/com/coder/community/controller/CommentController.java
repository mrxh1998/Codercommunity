package com.coder.community.controller;

import com.coder.community.entity.Comment;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Event;
import com.coder.community.entity.User;
import com.coder.community.event.EventProducer;
import com.coder.community.service.CommentService;
import com.coder.community.service.DiscussPostService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.HostHolder;
import com.google.code.kaptcha.Producer;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer producer;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable(name = "discussPostId")int discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setMap("postId",discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.selectById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }
        else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        producer.fireEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
