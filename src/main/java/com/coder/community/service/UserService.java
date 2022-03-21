package com.coder.community.service;

import com.coder.community.dao.UserMapper;
import com.coder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    public User selectById(int id){
        return userMapper.selectById(id);
    }
}
