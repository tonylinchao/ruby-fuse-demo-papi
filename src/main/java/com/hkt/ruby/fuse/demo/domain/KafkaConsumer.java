package com.hkt.ruby.fuse.demo.domain;

public class KafkaConsumer {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "KafkaConsumer{" +
                "topic='" + topic + '\'' +
                '}';
    }
}
