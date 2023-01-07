package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Product;
import com.coder.community.entity.User;
import com.coder.community.service.ProductService;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.DateUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping(path = "/product")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    ProductService productService;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    public String addProduct(String name, MultipartFile productImage,String introduction){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        //保存图片文件
        String imagesStr = "";
        if(productImage != null && !StringUtils.isBlank(productImage.getOriginalFilename())){
            String originalFilename = productImage.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if(StringUtils.isBlank(suffix)){
                throw new RuntimeException("上传文件失败，服务器发生异常");
            }
            String filename = CommunityUtil.generateUUID()+suffix;
            imagesStr = filename;
            //确定文件存放路径
            File dest = new File(uploadPath+"/"+filename);
            try {
                productImage.transferTo(dest);
            } catch (IOException e) {
                logger.error("上传文件失败"+e.getMessage());
                throw new RuntimeException("上传文件失败，服务器发生异常",e);
            }
        }
        imagesStr = domain + contextPath + "/product/image/"+imagesStr;
        //保存
        Product product = new Product();
        product.setProductName(name);
        product.setImageUrl(imagesStr);
        product.setIntroduction(introduction);
        product.setNewTime(new Date());
        productService.insertProduct(product);
        //报错情况统一处理
        return "redirect:/index";
    }
}
