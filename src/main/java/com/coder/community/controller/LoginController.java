package com.coder.community.controller;

import com.coder.community.entity.User;
import com.coder.community.service.LoginTicketService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public  String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> register = userService.register(user);
        if(register == null || register.isEmpty()){
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else{
            model.addAttribute("usernameMsg",register.get("usernameMessage"));
            model.addAttribute("passwordMsg",register.get("passwordMessage"));
            model.addAttribute("mailMsg",register.get("mailMessage"));
            return "/site/register";
        }
    }

   // 激活用户http//localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userid}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userid") int userid,@PathVariable("code") String code){
        int activation = userService.activation(userid, code);
        if(activation == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功！您的账号已经可以正常使用了");
            model.addAttribute("target","/login");
        }
        else if(activation == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已激活！");
            model.addAttribute("target","/index");
        }
        else{
            model.addAttribute("msg","激活失败，您的激活码不正确！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    //登陆页面
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(Model model){
        return "/site/login";
    }

    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
         //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //session.setAttribute("kaptcha",text);
        //验证码的归属
        String owner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",owner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //存验证码redis
        String redisKey = RedisKeyUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

   @RequestMapping(path="/login",method = RequestMethod.POST)
    public String login(Model model,String username,String password,String code,boolean rememberMe,
                        /*HttpSession session,*/HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String katpchaOwner){
       //String katpcha = (String)session.getAttribute("kaptcha");
       String katpcha = null;
       if(StringUtils.isNoneBlank(katpchaOwner)){
           String redisKey = RedisKeyUtil.getKaptchaKey(katpchaOwner);
           katpcha  = (String)redisTemplate.opsForValue().get(redisKey);
       }
       if(StringUtils.isBlank(code) || StringUtils.isBlank(katpcha) || !katpcha.equalsIgnoreCase(code)){
           model.addAttribute("codeMsg","验证码不正确");
           return "/site/login";
       }
       //检查账号密码
       int expiredSeconds = rememberMe?CommunityConstant.REMEMBER_EXPIRED_SECONDS:CommunityConstant.DEFAULT_EXPIRED_SECONDS;
       Map<String, Object> login = loginTicketService.login(username, password, expiredSeconds);
       if(login.containsKey("ticket")){
           Cookie cookie = new Cookie("ticket",(String)login.get("ticket"));
           cookie.setPath(contextPath);
           cookie.setMaxAge(expiredSeconds);
           response.addCookie(cookie);
          return "redirect:/index";
       }
       else{
           model.addAttribute("usernameMsg",login.get("usernameMessage"));
           model.addAttribute("passwordMsg",login.get("passwordMessage"));
           return "/site/login";
       }
   }
   @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        loginTicketService.logout(ticket);
        return "redirect:/index";
   }
}
