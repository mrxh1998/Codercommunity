package com.coder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.coder.community.entity.Event;
import com.coder.community.entity.Message;
import com.coder.community.service.MessageService;
import com.coder.community.util.CommunityConstant;
import com.coder.community.util.CommunityUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE,TOPIC_TOP_POST})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value()==null){
            logger.error("消息内容为空！");
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
        Message message = new Message();
        message.setToId(event.getEntityUserId());
        message.setFromId(1);
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getMap().isEmpty()){
            for(Map.Entry<String,Object> map : event.getMap().entrySet()){
                content.put(map.getKey(),map.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
