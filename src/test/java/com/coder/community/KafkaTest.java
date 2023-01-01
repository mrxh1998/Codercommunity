package com.coder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {
    @Autowired
    Producer producer;
    @Test
    public void testKafka() {
        producer.sendMessage("test","你好");
        producer.sendMessage("test","在吗");
        producer.sendMessage("test","sadasdasdasdsad");
        try {
            Thread.sleep(1000*100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
@Component
class Producer{
    @Autowired
    KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}

@Component
class Consumer{
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}