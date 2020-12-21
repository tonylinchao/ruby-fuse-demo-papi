package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.domain.KafkaProducer;
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
     * @param kafkaproducer Kafka producer message.
     */
    @PostMapping(path="/schema", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R avroSchema(@RequestBody KafkaProducer kafkaproducer) {

        logger.debug("Request to publish event topic: [" + kafkaproducer.getTopic() + "], message: [" + kafkaproducer.getMessage() +"].");
        Exchange result = fluentProducerTemplate
                .withHeader("topic", kafkaproducer.getTopic())
                .withHeader("message", kafkaproducer.getMessage())
                .to("direct:publish-event").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }


    /**
     * Publish Event
     *
     * @param kafkaproducer Kafka producer message.
     */
    @PostMapping(path="/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R publishEvent(@RequestBody KafkaProducer kafkaproducer) {

		logger.debug("Request to publish event topic: [" + kafkaproducer.getTopic() + "], message: [" + kafkaproducer.getMessage() +"].");
		Exchange result = fluentProducerTemplate
			    .withHeader("topic", kafkaproducer.getTopic())
                .withHeader("message", kafkaproducer.getMessage())
			    .to("direct:publish-event").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Publish Event
     *
     * @param topic Event Topic.
     */
    @GetMapping(path="/subscribe/{topic}")
    public R eventConsumer(@RequestParam(value="topic",required=false) String topic) {

        logger.debug("Request to sublic event topic: [" + topic + "].");
        Exchange result = fluentProducerTemplate
                .withHeader("topic", topic)
                .to("direct:event-consumer").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }
}
