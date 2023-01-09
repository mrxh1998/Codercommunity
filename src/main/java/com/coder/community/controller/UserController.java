package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import com.coder.community.entity.*;
import com.coder.community.service.*;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private FollowService followService;
    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }
        String filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }
        //http:localhost:8080/community/user/header/xxx.png
        String header = domain + contextPath + "/user/header/" + filename;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), header);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = uploadPath + "/" + filename;
        //声明文件格式
        String suffix = filename.substring(filename.lastIndexOf('.'));
        response.setContentType("image/" + suffix);
        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String passwordCheck, Model model) {
        if (!newPassword.equals(passwordCheck)) {
            model.addAttribute("checkPasswordMsg", "两次输入密码不一致");
        }
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "密码错误");
            return "/site/setting";
        }
        String password = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), password);
        user.setPassword(password);
        hostHolder.setUser(user);
        return "redirect:/index";
    }

    /**
     * 个人主页
     *
     * @param
     * @param
     * @return "/site/profile"
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        //被赞数目
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", userLikeCount);
        //粉丝数目
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //关注状态
        boolean followStatus = false;
        if (hostHolder.getUser() != null) {
            followStatus = followService.isFollowEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followStatus", followStatus);
        return "/site/profile";
    }

    @RequestMapping(path = "/userPosts/{userId}", method = RequestMethod.GET)
    public String getUserPosts(Page page, Model model, @PathVariable int userId) {
        page.setRows(discussPostService.selectDiscussPostRows(0, 0,null));
        page.setPath("/user/userPosts/" + userId);
        List<DiscussPost> list = discussPostService.selectDiscussPosts(userId, page.getOffset(), page.getLimit(), 0,null);
        int postCount = discussPostService.selectDiscussPostRows(userId, 0,null);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        User user = userService.selectById(userId);
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("video", post.getVideo());
                List imageList = new ArrayList();
                if (!StringUtils.isBlank(post.getImages())) {
                    String[] split = post.getImages().split("[+]");
                    for (String image : split) {
                        imageList.add(domain + contextPath + "/discuss/image/" + image);
                    }
                }
                map.put("imageList", imageList);
                long likeCount = likeService.entityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        //查询产品列表
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("user", user);
        model.addAttribute("postCount", postCount);
        return "/site/myPost";
    }

    @RequestMapping(path = "/userReply/{userId}", method = RequestMethod.GET)
    public String getUserReply(Page page, Model model, @PathVariable int userId) {
        List<Comment> comments = commentService.findCommentByUserId(userId);
        //选择实体为帖子的id先行选择
        Set<Integer> discussPostIds = comments.stream().filter(e -> e.getEntityType() == 1).map(Comment::getEntityId)
                .collect(Collectors.toSet());
        //获取回复的id集合
        Set<Integer> replyIds = comments.stream().filter(e -> e.getEntityType() == 2).map(Comment::getEntityId)
                .collect(Collectors.toSet());
        //根据回复id集合查询所有的帖子id
        discussPostIds.addAll(commentService.selectCommentByIds(replyIds).stream().map(Comment::getEntityId)
                .collect(Collectors.toSet()));
        List<DiscussPost> discussPostsByIds = discussPostService
                .findDiscussPostsByIds(discussPostIds, page.getOffset(), page.getLimit());
        page.setRows(discussPostService.countDiscussPostsByIds(discussPostIds));
        page.setPath("/user/userReply/" + userId);
        User user = userService.selectById(userId);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPostsByIds != null && !discussPostIds.isEmpty()) {
            for (DiscussPost post : discussPostsByIds) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("video", post.getVideo());
                List imageList = new ArrayList();
                if (!StringUtils.isBlank(post.getImages())) {
                    String[] split = post.getImages().split("[+]");
                    for (String image : split) {
                        imageList.add(domain + contextPath + "/discuss/image/" + image);
                    }
                }
                map.put("imageList", imageList);
                long likeCount = likeService.entityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        //查询产品列表
        model.addAttribute("user", user);
        model.addAttribute("postCount", page.getRows());
        model.addAttribute("discussPosts", discussPosts);
        return "/site/my-reply";
    }

    @RequestMapping(path = "/userCollect/{userId}", method = RequestMethod.GET)
    public String getUserCollect(Page page, Model model, @PathVariable int userId) {
        User user = userService.selectById(userId);
        page.setRows(discussPostService.getUserCollectCount(userId).intValue());
        page.setPath("/user/userPosts/" + userId);
        List<DiscussPost> list = discussPostService.findUserCollect(user, page.getOffset(), page.getLimit());
        int postCount = discussPostService.getUserCollectCount(userId).intValue();
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("video", post.getVideo());
                List imageList = new ArrayList();
                if (!StringUtils.isBlank(post.getImages())) {
                    String[] split = post.getImages().split("[+]");
                    for (String image : split) {
                        imageList.add(domain + contextPath + "/discuss/image/" + image);
                    }
                }
                map.put("imageList", imageList);
                long likeCount = likeService.entityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        //查询产品列表
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("user", user);
        model.addAttribute("postCount", postCount);
        return "/site/myCollect";
    }
}
