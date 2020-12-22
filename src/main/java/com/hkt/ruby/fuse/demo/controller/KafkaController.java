package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.domain.EventRecords;
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
     * Publish Event
     *
     * @param topic Kafka topic
     * @param records Kafka event records.
     */
    @PostMapping(path="/produce/topics/{topic}")
    public R publishEvents(@RequestParam(value="topic",required=false) String topic,
                          @RequestBody EventRecords records) {

		logger.debug("Request to publish event topic: [" + topic + "], records: [" + records.toString() +"].");
		Exchange result = fluentProducerTemplate
			    .withHeader("topic", topic)
                .withBody(records)
			    .to("direct:publish-event").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Publish Event
     *
     * @param topic Event Topic.
     */
    @GetMapping(path="/consume")
    public R eventConsumer(@RequestParam(value="topic",required=false) String topic) {

        logger.debug("Request to sublic event topic: [" + topic + "].");
        Exchange result = fluentProducerTemplate
                .withHeader("topic", topic)
                .to("direct:event-consumer").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }
}
