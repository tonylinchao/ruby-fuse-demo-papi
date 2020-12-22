package com.hkt.ruby.fuse.demo.domain;

import java.util.List;

public class EventRecord {

    private String key;
    private String value;
    private int partition;
    private List<EventHeader> headers;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public List<EventHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<EventHeader> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "EventRecord{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", partition=" + partition +
                ", headers=" + headers +
                '}';
    }
}
