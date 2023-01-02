package com.coder.community.controller;

import com.coder.community.entity.Comment;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.CommentService;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    LikeService likeService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addPost(String title, String content, MultipartFile image){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setCreateTime(new Date());
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        discussPostService.insertDiscussPost(post);
        //报错情况统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String postDetail(@PathVariable("discussPostId") int id, Model model, Page page){
        DiscussPost discussPost = discussPostService.selectById(id);
        model.addAttribute("post",discussPost);
        User user = userService.selectById(discussPost.getUserId());
        model.addAttribute("user",user);
        //该帖子的赞数和赞的状态返回
        model.addAttribute("likeCount",likeService.entityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));
        int likeStatus = hostHolder.getUser()==null?0:
                likeService.entityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPost.getId());
        model.addAttribute("likeStatus",likeStatus);
        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+id);
        page.setRows(discussPost.getCommentCount());
        //所有评论
        List<Comment> comments = commentService.findCommentsByEntity(ENTITY_TYPE_POST,
                id, page.getOffset(), page.getLimit());
        //评论视图列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(comments != null){
            for(Comment comment : comments){
                User user1 = userService.selectById(comment.getUserId());
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("user",user1); //评论的用户
                commentVo.put("comment",comment);//评论的具体信
                //存评论的赞和状态
                commentVo.put("likeCount",likeService.entityLikeCount(ENTITY_TYPE_COMMENT,comment.getId()));
                likeStatus = hostHolder.getUser()==null?0:
                        likeService.entityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);
                //每条评论的回复
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                //每条评论的回复视图列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //每条回复的用户
                        replyVo.put("user",userService.selectById(reply.getUserId()));
                        //每条回复的具体信息
                        replyVo.put("reply",reply);
                        //回复目标
                        User target = reply.getTargetId()==0?null:userService.selectById(reply.getTargetId());
                        replyVo.put("target",target);
                        //存回复的赞和状态
                        replyVo.put("likeCount",likeService.entityLikeCount(ENTITY_TYPE_COMMENT,reply.getId()));
                        likeStatus = hostHolder.getUser()==null?0:
                                likeService.entityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                        //把每个回复视图加入回复视图列表
                        replyVoList.add(replyVo);
                    }
                }
                //把当前评论的回复视图列表加入评论视图中
                commentVo.put("replys",replyVoList);
                //评论的回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                //把每个评论视图加入评论视图列表
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}