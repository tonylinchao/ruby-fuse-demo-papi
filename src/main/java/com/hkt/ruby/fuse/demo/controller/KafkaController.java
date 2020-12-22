package com.hkt.ruby.fuse.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkt.ruby.fuse.demo.domain.EventRecords;
import com.hkt.ruby.fuse.demo.route.KafkaRouter;
import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Kafka Event Operation
 *
 * @author Tony C Lin
 */
@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    /**
     * Publish Event via Kafka Rest Bridge
     *
     * @param topic Kafka topic
     * @param records Kafka event records.
     */
    @PostMapping(path="/produce/topics/{topic}")
    public R produceEvents(@PathVariable(value="topic",required=true) String topic,
                          @RequestBody EventRecords records) throws Exception {

		logger.debug("Request to publish event topic: [" + topic + "], records: [" + records.toString() +"].");

		ObjectMapper objectMapper = new ObjectMapper();
		Exchange result = fluentProducerTemplate
			    .withHeader("topic", topic)
                .withBody(objectMapper.writeValueAsString(records))
			    .to("direct:produce-events").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Subscribe Event via Kafka Rest Bridge
     *
     * @param topic Kafka topic
     */
    @PostMapping(path="/consume/topics/{topic}")
    public R consumeEvents(@PathVariable(value="topic",required=true) String topic,
                           @RequestParam(value="timeout",required=false) String timeout,
                           @RequestParam(value="maxBytes",required=false) String maxBytes) throws Exception {

        logger.debug("Request to publish event topic: [" + topic + "].");

        ObjectMapper objectMapper = new ObjectMapper();
        Exchange result = fluentProducerTemplate
                .withHeader("topic", topic)
                .to("direct:consume-events").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

}
