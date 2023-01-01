package com.coder.community.service;

import com.coder.community.dao.LoginTicketMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.LoginTicket;
import com.coder.community.entity.User;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class LoginTicketService {
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        HashMap<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMessage","该账号不存在");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("usernameMessage","该账号未激活");
            return map;
        }
        String s = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(s)){
            map.put("passwordMessage","密码错误");
            return map;
        }
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
       // LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        //return loginTicket;
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        return loginTicket;
    }
}
