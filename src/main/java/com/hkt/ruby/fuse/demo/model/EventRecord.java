package com.hkt.ruby.fuse.demo.model;

import lombok.Data;

/**
 * Kafka Event Record
 *
 * @author Tony C Lin
 */
@Data
public class EventRecord {

    private String key;
    private String value;
    private int partition;
}
