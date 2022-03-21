package com.coder.community;

import com.coder.community.controller.HomeController;
import com.coder.community.dao.AlphaDao;
import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.service.AlphaService;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Test
	void contextLoads() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		alphaDao = applicationContext.getBean("AlphaMybatis",AlphaDao.class);
		alphaDao.select();
	}
	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}
	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
	@Autowired
	@Qualifier("AlphaMybatis")
	private AlphaDao alphaDao;
	@Test
	public void testDI(){
		System.out.println(alphaDao);
	}
	@Autowired
	UserMapper userMapper;
	@Test
	public void testselectUser(){
		System.out.println(userMapper.selectById(101));
	}
	@Autowired
	DiscussPostMapper discussPostMapper;
	@Test
	public void testSelectDiscussPost(){
		System.out.println(discussPostMapper.selectDiscussPostsRows(0));
		System.out.println(discussPostMapper.selectDiscussPostsRows(1));
		System.out.println(discussPostMapper.selectDiscussPosts(0,1,5));
		System.out.println(discussPostMapper.selectDiscussPosts(1,0,5));
	}
	@Autowired
	DiscussPostService discussPostService;
	@Autowired
	UserService userService;
	@Test
	public void testUserDiscussService(){
		System.out.println(discussPostMapper.selectDiscussPostsRows(111));
		System.out.println(discussPostService.selectDiscussPosts(111,0,5));
		System.out.println(userService.selectById(111).toString());
	}
	@Autowired
	HomeController homeController;
	@Test
	public void testHomeController(){
		System.out.println(homeController.toString());
	}
}
