package com.hkt.ruby.fuse.demo.domain;

import java.util.Map;

public class Message {
    private String topic;

    private String desc;

    private Map<String, Object> message;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String, Object> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", desc='" + desc + '\'' +
                ", message=" + message +
                '}';
    }
}
