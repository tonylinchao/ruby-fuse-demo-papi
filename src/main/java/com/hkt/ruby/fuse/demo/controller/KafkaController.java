package com.hkt.ruby.fuse.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkt.ruby.fuse.demo.model.EventRecords;
import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Kafka Event Operation
 *
 * @author Tony C Lin
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
@Tag(name = "Kafka Bridge", description = "The Kafka Bridge API")
public class KafkaController {

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    /**
     * Publish Event via Kafka Rest Bridge
     *
     * @param topic Kafka topic
     * @param records Kafka event records.
     */
    @PostMapping(path="/producer/topics/{topic}", produces={"application/json"})
    public R produceEvents(@PathVariable(value="topic",required=true) String topic,
                          @RequestBody EventRecords records) throws Exception {

		log.debug("Request to publish event topic: [" + topic + "], records: [" + records.toString() +"].");

		ObjectMapper objectMapper = new ObjectMapper();
		Exchange result = fluentProducerTemplate
			    .withHeader("topic", topic)
                .withBody(objectMapper.writeValueAsString(records))
                .to("json-validator:schemas/kafka-publish-event-request.json")
			    .to("direct:produce-events").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Subscribe Event via Kafka Rest Bridge
     *
     * @param groupId Consumer group id
     * @param name Consumer instance name
     */
    @GetMapping(path="/consumer/{groupid}/{name}", produces={"application/json"})
    public R consumeEvents(@PathVariable(value="groupid",required=true) String groupId,
                           @PathVariable(value="name",required=true) String name) {

        log.debug("Request to publish event groupid: [" + groupId + "], instance: [" + name + "]." );

        Exchange result = fluentProducerTemplate
                .withHeader("groupid", groupId)
                .withHeader("name", name)
                .to("direct:consume-events").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Kafka consumer to call Strimzi Kafka bootstrap directly
     */
    @GetMapping(path="/consumer", produces={"application/json"})
    public R kafkaConsumer() {

        Exchange result = fluentProducerTemplate.to("direct:kafka-consumer").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }
}
