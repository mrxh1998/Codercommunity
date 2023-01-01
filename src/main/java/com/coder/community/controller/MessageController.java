package com.coder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.coder.community.annotation.LoginRequired;
import com.coder.community.entity.Message;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.MessageService;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.countConversations(user.getId()));
        //得到所有会话框对应的消息
        List<Message> Conversations = messageService.findUserConversations(user.getId(), page.getOffset(),
                page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        for(Message conversation:Conversations){
            Map<String,Object> map = new HashMap<>();
            //该对话相关信息
            map.put("conversation",conversation);
            //对话的目标用户      如果当前用户是会话的发起者，目标用户id应为对话的To方
            int userId = user.getId()==conversation.getFromId()?conversation.getToId():conversation.getFromId();
            map.put("target",userService.selectById(userId));
            //该对话总的私信数
            map.put("letterCount",messageService.countLetters(conversation.getConversationId()));
            //该对话未读私信条数
            map.put("unreadCount",messageService.countUnreadLetters(user.getId(),conversation.getConversationId()));
            conversations.add(map);
        }
        int letters = messageService.countUnreadLetters(user.getId(), null);
        model.addAttribute("unreadCount",letters);
        model.addAttribute("conversations",conversations);
        int unReadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("unreadNoticeCount",unReadNoticeCount);

        return "/site/letter";
    }
    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId,Page page,Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.countLetters(conversationId));

        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        //设置已读
        List<Integer> letterIds = getLetterIds(letters);
        if(!letterIds.isEmpty()){
            messageService.readMessage(letterIds);
        }
        List<Map<String,Object>>letterList = new ArrayList<>();
        for(Message letter:letters){
            Map<String,Object> map = new HashMap<>();
            map.put("letter",letter);
            map.put("fromUser",userService.selectById(letter.getFromId()));
            letterList.add(map);
        }
        model.addAttribute("letters",letterList);
        model.addAttribute("target",getLetterTarget(conversationId));

        return "/site/letter-detail";
    }
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for(Message message:letterList){
                if(message.getStatus()==0 && hostHolder.getUser().getId()==message.getToId()){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    private User getLetterTarget(String conversationId){
        String[] s = conversationId.split("_");
        User user = hostHolder.getUser();
        int id;
        if(user.getId() == Integer.parseInt(s[0])){
            id = Integer.parseInt(s[1]);
        }
        else{
            id = Integer.parseInt(s[0]);
        }
        return userService.selectById(id);
    }

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String content,String toName){
        User user = userService.findUserByName(toName);
        if(user == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setCreateTime(new Date());
        message.setStatus(0);
        message.setToId(user.getId());
        if(message.getToId()<message.getFromId()){
            message.setConversationId(message.getToId()+"_"+ message.getFromId());
        }
        else{
            message.setConversationId(message.getFromId()+"_"+ message.getToId());
        }
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    @LoginRequired
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        Map<String,Object> noticeVO;
        //查询评论类
        Message latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        noticeVO = new HashMap<>();
        noticeVO.put("notice",latestNotice);
        if(latestNotice != null){

            String  content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            Map<String,Object> map = JSONObject.parseObject(content, HashMap.class);

            noticeVO.put("user",userService.selectById((Integer) map.get("userId")));
            noticeVO.put("entityType",map.get("entityType"));
            noticeVO.put("entityId",map.get("entityId"));
            noticeVO.put("postId",map.get("postId"));
            noticeVO.put("noticeCount",messageService.findNoticeCount(user.getId(), TOPIC_COMMENT));
            noticeVO.put("unreadNoticeCount",messageService.findUnreadNoticeCount(user.getId(),TOPIC_COMMENT));
        }
        model.addAttribute("commentNotice",noticeVO);
        //点赞
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        noticeVO = new HashMap<>();
        noticeVO.put("notice",latestNotice);
        if(latestNotice != null){

            String  content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            Map<String,Object> map = JSONObject.parseObject(content, HashMap.class);

            noticeVO.put("user",userService.selectById((Integer) map.get("userId")));
            noticeVO.put("entityType",map.get("entityType"));
            noticeVO.put("entityId",map.get("entityId"));
            noticeVO.put("postId",map.get("postId"));
            noticeVO.put("noticeCount",messageService.findNoticeCount(user.getId(), TOPIC_LIKE));
            noticeVO.put("unreadNoticeCount",messageService.findUnreadNoticeCount(user.getId(),TOPIC_LIKE));
        }
        model.addAttribute("likeNotice",noticeVO);
        //关注
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        noticeVO = new HashMap<>();
        noticeVO.put("notice",latestNotice);
        if(latestNotice != null){

            String  content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            Map<String,Object> map = JSONObject.parseObject(content, HashMap.class);

            noticeVO.put("user",userService.selectById((Integer) map.get("userId")));
            noticeVO.put("entityType",map.get("entityType"));
            noticeVO.put("entityId",map.get("entityId"));
            noticeVO.put("noticeCount",messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW));
            noticeVO.put("unreadNoticeCount",messageService.findUnreadNoticeCount(user.getId(),TOPIC_FOLLOW));
        }
        model.addAttribute("followNotice",noticeVO);
        //查询未读消息数量
        int unReadLetterCount = messageService.countUnreadLetters(user.getId(), null);
        int unReadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("unreadLetterCount",unReadLetterCount);
        model.addAttribute("unreadNoticeCount",unReadNoticeCount);
        return "/site/notice";
    }

    //系统通知详情页面
    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Model model,Page page){
        User user = hostHolder.getUser();
        page.setPath("/notice/detail/"+topic);
        page.setLimit(5);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeList = new ArrayList<>();
        if(notices!=null){
            for(Message notice:notices){
                Map<String,Object> noticeVO = new HashMap<>();
                String  content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> map = JSONObject.parseObject(content, HashMap.class);

                noticeVO.put("notice",notice);
                noticeVO.put("user",userService.selectById((Integer) map.get("userId")));
                noticeVO.put("entityType",map.get("entityType"));
                noticeVO.put("entityId",map.get("entityId"));
                noticeVO.put("postId",map.get("postId"));
                //通知的系统用户名
                noticeVO.put("fromUser",userService.selectById(notice.getFromId()));
                noticeList.add(noticeVO);
            }
        }
        model.addAttribute("notices",noticeList);
        //设置已读
        List<Integer> ids = getLetterIds(notices);
        if(ids.size()!=0){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}
