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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    @RequestMapping(path="/image/{filename}",method = RequestMethod.GET)
    public void getImage(@PathVariable("filename")String filename, HttpServletResponse response){
        filename = uploadPath + "/"+filename;
        //声明文件格式
        String suffix = filename.substring(filename.lastIndexOf('.'));
        response.setContentType("image/"+suffix);
        try(
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename);
        ) {
            byte[]buffer = new byte[1024];
            int b = 0;
            while((b=fileInputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取图片"+e.getMessage());
        }
    }
}
