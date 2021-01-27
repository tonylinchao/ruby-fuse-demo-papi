package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.properties.KafkaProperties;
import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import com.hkt.ruby.fuse.demo.utils.SSLUtils;
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
        // skip hostname checking
        httpComponent = SSLUtils.skipHostnameCheck(httpComponent);

        // Publish events via Kafka Bridge
        from("direct:produce-events").routeId("direct-produce-events")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant(Constants.KAFKA_PRODUCER_CONTENT_TYPE_JSON))
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
                        + "?bridgeEndpoint=true"
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


//        from("timer://foo?period=1000")
//                .routeId("Producer Route")
//                .setBody(simple("Hi, this is Camel-Strimzi example from ft-a environment"))
//                .to("kafka:ruby-topic");
//
//        from("kafka:ruby-topic")
//                .routeId("Consumer Route")
//                .log("${body}");

    }

}
