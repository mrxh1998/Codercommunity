package com.coder.community.service;

import com.coder.community.dao.UserMapper;
import com.coder.community.entity.User;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.MailClient;
import com.coder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    UserMapper userMapper;
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User selectById(int id){
//        return userMapper.selectById(id);
        User user = getFromCache(id);
        if(user == null){
            user = setCache(id);
        }
        return user;
    }

    public Map<String,Object> register(User user){
        HashMap<String, Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("User不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("mailMessage","邮箱不能为空");
            return map;
        }
        //验证账号是否存在
        if(userMapper.selectByName(user.getUsername()) != null){
            map.put("usernameMessage","该账号存在");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail()) != null){
            map.put("mailMessage","该邮箱已存在");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationcode(CommunityUtil.generateUUID());
        user.setHeaderurl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreatetime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活用户http//localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/"+user.getId()+"/"+user.getActivationcode();
        context.setVariable("url",url);
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活邮件",process);
        return map;
    }
    public int activation(int userid,String code){
        User user = userMapper.selectById(userid);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationcode().equals(code)){
            userMapper.updateStatus(userid,1);
            clearCache(userid);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FALURE;
        }
    }
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
    public int updateHeader(int userId,String headerUrl){
        int i = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return i;
    }
    public int updatePassword(int userId,String password){
        int i = userMapper.updatePassword(userId, password);
        clearCache(userId);
        return i;
    }
    //从缓存种取值
    private User getFromCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user =(User) redisTemplate.opsForValue().get(redisKey);
        return user;
    }
    //缓存无数据时加载数据到缓存
    private User setCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //当数据变更时删除换粗
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
