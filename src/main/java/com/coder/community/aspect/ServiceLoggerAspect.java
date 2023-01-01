package com.coder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLoggerAspect {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggerAspect.class);
    //连接点              返回值
    @Pointcut("execution(* com.coder.community.service.*.*(..))")
    public void pointCut(){

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        //用户[1.2.3.4],在[xxx],访问了[com.coder.community.service.xxx()]
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null){
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s]",ip,now,target));
    }
}
