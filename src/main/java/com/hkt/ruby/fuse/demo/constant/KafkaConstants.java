package com.hkt.ruby.fuse.demo.constant;

public interface KafkaConstants {

    /**
     * Content-Type for kafka producer
     */
    String KAFKA_PRODUCER_CONTENT_TYPE_JSON = "application/vnd.kafka.json.v2+json";

    /**
     * Content-Type for kafka consumer
     */
    String KAFKA_CONSUMER_CONTENT_TYPE_JSON = "application/vnd.kafka.v2+json";

    /**
     * 3scale Gateway API key
     */
    String GATEWAY_API_KEY = "x-api-key";

    /**
     * Accept header
     */
    String HEADER_ACCEPT = "Accept";

    /**
     * Host header
     */
    String HEADER_HOST = "Host";

    /**
     * Host Json content-type
     */
    String HEADER_CONTENT_TYPE_JSON = "application/json";

}
