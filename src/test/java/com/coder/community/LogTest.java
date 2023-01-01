package com.coder.community;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.LoginTicketMapper;
import com.coder.community.dao.MessageMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.LoginTicket;
import com.coder.community.entity.Message;
import com.coder.community.service.CommentService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.MailClient;
import com.coder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LogTest implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger("LogTest");

    @Test
    public void testLog(){
        System.out.println(logger.getName());
        logger.debug("debug.Log");
        logger.info("info Log");
        logger.warn("warn Log");
        logger.error("error Log");
    }
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine themeleaf;
    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Autowired
    CommentService commentService;
    @Test
    public void sendMail(){
        int n = 10;
        while(n--!=0){mailClient.sendMail("763302385@qq.com","团子帅炸了","Test"+n+"团子好帅");

        };
    }
    @Test
    public void SendHttpMail(){
        Context context = new Context();
        context.setVariable("username","团团");
        String process = themeleaf.process("/mail/activation", context);
        mailClient.sendMail("763302385@qq.com","test",process);
    }
    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("122");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());
        loginTicket.setUserId(111);
        loginTicketMapper.insertLoginTicket(loginTicket);
        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("122");
        System.out.println(loginTicket1.toString());
        loginTicketMapper.updateStatus("122",1);
        LoginTicket loginTicket2 = loginTicketMapper.selectByTicket("122");
        System.out.println(loginTicket2.toString());
    }
    @Test
    public void filter(){
        String str = "这里可以赌**-+博可以嫖-+娼可以吸2132313毒，哈哈哈";
        String filter = sensitiveFilter.filter(str);
        System.out.println(filter);
    }
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    MessageMapper messageMapper;
    @Test
    public void insertDiscussPost(){
//        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
//        for(Message message : messages){
//            System.out.println(message);
//        }
        int i = messageMapper.selectConversationCount(111);
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        int i1 = messageMapper.selectLetterCount("111_112");
        int i2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println("会话数量"+i);
        System.out.println("私信数量"+i1);
        System.out.println("未读私信数量"+i2);
        for(Message message:messages){
            System.out.println(message);
        }
    }

    @Test
    public void test() throws IOException {
        AlphaTest alphaTest = new AlphaTest();
        PriorityQueue<ans> answer = alphaTest.getAnswer("D:/Desktop/思政.txt", "马克思主义中国化具体是什么");
        ans peek = answer.peek();//取匹配度最大的元素，但不出队；
        // ans poll = ans.poll(); 取匹配度最大的元素，并且出队
        String string =  peek.answer;  //字符串
        System.out.println(string);
        HashMap<Object,Object> map = new HashMap<>();
    }
}
