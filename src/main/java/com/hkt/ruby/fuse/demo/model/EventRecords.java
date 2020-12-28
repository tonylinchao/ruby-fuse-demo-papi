package com.hkt.ruby.fuse.demo.model;

import lombok.Data;
import java.util.List;

/**
 * Kafka Event Records
 *
 * @author Tony C Lin
 */
@Data
public class EventRecords {

    private List<EventRecord> records;
}
