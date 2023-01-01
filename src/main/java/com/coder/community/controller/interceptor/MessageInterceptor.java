package com.coder.community.controller.interceptor;

import com.coder.community.entity.User;
import com.coder.community.service.MessageService;
import com.coder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
            int unreadLettersCount = messageService.countUnreadLetters(user.getId(), null);
            modelAndView.addObject("allUnreadCount",unreadLettersCount+unreadNoticeCount);
        }
    }
}
