package com.hkt.ruby.fuse.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

public class KafkaRouter extends RouteBuilder {

    @Value("${kafka.servier.hostname}")
    private String kafkaServerHost;

    @Value("${kafka.server.port}")
    private String kafkaServerPort;

    @Value("${kafka.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${kafka.ssl.keystore.password}")
    private String keystorePass;

    @Value("${kafka.ssl.protocol}")
    private String sslProtocol;

    @Value("${kafka.ssl.groupId}")
    private String kafkaGroupId;

    @Override
    public void configure() throws Exception {

        // Publish event via Kafka Bridge
        from("kafka:my-topic?brokers=my-cluster-kafka-bootstrap-ruby-dev.apps.openshift3.com:443"+
                "&sslTruststoreLocation=classpath:local-truststore.jks" +
                "&sslTruststorePassword=password" +
                "&securityProtocol=SSL" +
                "&groupId=my-topic-consumer-group")
                .convertBodyTo(String.class)
                //.process(customerProcessor)
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}")
                .marshal().json()
                .end();


        // Publish event to on-prem kafka
        from("direct:publish-event").routeId("direct-publish-event")
                .toD("kafka:my-topic?brokers=my-cluster-kafka-bootstrap-ruby-dev.apps.openshift3.com:443"+
                        "&sslTruststoreLocation=classpath:local-truststore.jks" +
                        "&sslTruststorePassword=password" +
                        "&securityProtocol=SSL" +
                        "&groupId=my-topic-consumer-group")
                .convertBodyTo(String.class)
                //.process(customerProcessor)
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}")
                .marshal().json()
                .end();


        // Subscribe event to on-prem kafka
//        from("direct:event-consumer").routeId("direct-event-consumer")
//                .toD("kafka: ${in.header.topic}" +
//                        "?brokers=" + kafkaServerHost + ":" + kafkaServerPort
//                        + "&sslTruststoreLocation=" + keystoreLocation
//                        + "&sslTruststorePassword=" + keystorePass
//                        + "&securityProtocol=" + sslProtocol
//                        + "&groupId=" + kafkaGroupId
//                 )
//                .convertBodyTo(String.class)
//                //.process(customerProcessor)
//                .log("Message received from Kafka : ${body}")
//                .log("    on the topic ${headers[kafka.TOPIC]}")
//                .log("    on the partition ${headers[kafka.PARTITION]}")
//                .log("    with the offset ${headers[kafka.OFFSET]}")
//                .log("    with the key ${headers[kafka.KEY]}")
//                .marshal().json()
//                .end();

        from("direct:event-consumer").routeId("direct-event-consumer")
                .toD("kafka:ruby-topic?brokers=my-cluster-kafka-bootstrap-ruby-kafka-uat.app3.osp.pccw.com:443"+
                        "&sslTruststoreLocation=/tmp/client-truststore.jks" +
                        "&sslTruststorePassword=password" +
                        "&securityProtocol=SSL" +
                        "&groupId=group1")
                .convertBodyTo(String.class)
                //.process(customerProcessor)
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}")
                .marshal().json()
                .end();



//        from("direct:event-consumer").routeId("direct-event-consumer")
//                .toD("kafka:ruby-topic?brokers=my-cluster-kafka-bootstrap-ruby-kafka-uat.app3.osp.pccw.com:443"+
//                    "&sslTruststoreLocation=/tmp/client-truststore.jks" +
//                    "&sslTruststorePassword=password" +
//                    "&securityProtocol=SSL" +
//                    "&groupId=group1")
//                .convertBodyTo(String.class)
//                //.process(customerProcessor)
//                .log("Message received from Kafka : ${body}")
//                .log("    on the topic ${headers[kafka.TOPIC]}")
//                .log("    on the partition ${headers[kafka.PARTITION]}")
//                .log("    with the offset ${headers[kafka.OFFSET]}")
//                .log("    with the key ${headers[kafka.KEY]}")
//                .marshal().json()
//                .end();
    }

}
