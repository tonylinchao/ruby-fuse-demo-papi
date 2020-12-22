package com.hkt.ruby.fuse.demo.domain;

import java.util.List;

public class EventRecords {
    private List<EventRecord> records;

    public List<EventRecord> getRecords() {
        return records;
    }

    public void setRecords(List<EventRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "EventRecords{" +
                "records=" + records +
                '}';
    }
}
