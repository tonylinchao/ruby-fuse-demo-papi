package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.properties.KafkaProperties;
import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import com.hkt.ruby.fuse.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.http4.HttpMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties({KafkaProperties.class, SystemProperties.class})
public class KafkaRoute extends RouteBuilder {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public void configure() throws Exception {

        HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
        httpComponent.setSslContextParameters(CommonUtils.sslContextParameters(systemProperties.getSsl().getTruststorePath(),
                systemProperties.getSsl().getTruststorePass()));

        // Publish events via Kafka Bridge
        from("direct:produce-events").routeId("direct-produce-events")
                .setHeader(Exchange.CONTENT_TYPE, constant(Constants.KAFKA_PRODUCER_CONTENT_TYPE_JSON))
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Constants.HEADER_ACCEPT, constant(Constants.KAFKA_CONSUMER_CONTENT_TYPE_JSON))
                .setHeader(Constants.GATEWAY_API_KEY, constant(kafkaProperties.getGateway().getApiKey()))
                .setHeader(Constants.HEADER_HOST, constant(kafkaProperties.getGateway().getHostname()))
                .toD("https4:" + kafkaProperties.getBridge().getBaseUrl() + "/topics/${in.header.topic}"
                        + "?bridgeEndpoint=true"
                        + "&throwExceptionOnFailure=false"
                        + "&connectTimeout=30000")
                .convertBodyTo(String.class)
                .log("${body}")
                .marshal().json()
                .end();

        // Subscribe events via Kafka Bridge
        from("direct:consume-events").routeId("direct-consume-events")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .setHeader(Constants.HEADER_ACCEPT, constant(Constants.KAFKA_PRODUCER_CONTENT_TYPE_JSON))
                .setHeader(Constants.GATEWAY_API_KEY, constant(kafkaProperties.getGateway().getApiKey()))
                .setHeader(Constants.HEADER_HOST, constant(kafkaProperties.getGateway().getHostname()))
                .toD("https4:" + kafkaProperties.getBridge().getBaseUrl() + "/consumers/${in.header.groupid}/instances/${in.header.name}/records"
                        + "?timeout" + kafkaProperties.getBridge().getSubscribeTimeout()
                        + "&max_bytes" + kafkaProperties.getBridge().getSubscribeMaxBytes()
                        + "&bridgeEndpoint=true"
                        + "&throwExceptionOnFailure=false"
                        + "&connectTimeout=30000")
                .convertBodyTo(String.class)
                .log("${body}")
                .marshal().json()
                .end();

        // Kafka consumer to call Strimzi Kafka bootstrap directly
        from("direct:kafka-consumer").routeId("direct-kafka-consumer")
                .toD("kafka:" + kafkaProperties.getTopic().getName()
                        + "?brokers=" + kafkaProperties.getBootstrap().getHostname()
                        + "&sslTruststoreLocation=" + kafkaProperties.getBootstrap().getTruststorePath()
                        + "&sslTruststorePassword=" + kafkaProperties.getBootstrap().getTruststorePass()
                        + "&securityProtocol=" + kafkaProperties.getBootstrap().getProtocol()
                        + "&groupId=" + kafkaProperties.getTopic().getConsumerGroup()
                )
                .convertBodyTo(String.class)
                .log("${body}")
                .marshal().json()
                .end();

    }

}
