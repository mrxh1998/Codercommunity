package com.coder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;
    private int userId;
    private int entityId;
    private int entityType;
    private int entityUserId;
    private Map<String,Object> map = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Event setMap(String key,Object value) {
        this.map.put(key,value);
        return this;
    }
}
