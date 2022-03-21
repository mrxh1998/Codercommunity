package com.coder.community.service;
import com.coder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    @Qualifier("AlphaMybatis")
    private AlphaDao alphaDao;
    public AlphaService(){
        System.out.println("实例化alphaservice");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化alphaservice");
    }
    @PreDestroy
    public void destory(){
        System.out.println("销毁alphaservice");
    }
    public String find(){
        return alphaDao.select();
    }
}
