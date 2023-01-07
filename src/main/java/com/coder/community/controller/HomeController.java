package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.entity.Product;
import com.coder.community.entity.User;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.LikeService;
import com.coder.community.service.ProductService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    LikeService likeService;
    @Autowired
    ProductService productService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page, @PathParam("productId") Integer productId){
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");
        int product_id = 0;
        if(productId != null){
            product_id = productId;
        }
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0,page.getOffset(),page.getLimit(),product_id);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!= null){
            for(DiscussPost post: list){
                int userId = post.getUserId();
                User user = userService.selectById(userId);
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",user);
                map.put("video",post.getVideo());
                List imageList = new ArrayList();
                if(!StringUtils.isBlank(post.getImages())){
                    String[] split = post.getImages().split("[+]");
                    for(String image : split){
                        imageList.add(domain + contextPath + "/discuss/image/"+image);
                    }
                }
                map.put("imageList",imageList);
                long likeCount = likeService.entityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        //查询产品列表
        List<Product> productList = productService.getAllProduct();
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("productList",productList);
        model.addAttribute("productId",product_id);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
