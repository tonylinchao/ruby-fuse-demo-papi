package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.KafkaConstants;
import com.hkt.ruby.fuse.demo.utils.SSLUtil;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaRouter extends RouteBuilder {

    @Value("${kafka.bridge.baseurl}")
    private String kafkaBridgeBaseUrl;

    @Value("${kafka.3scale.apikey}")
    private String gatewayApiKey;

    @Value("${ssl.keystore.path}")
    private String keystorePath;

    @Value("${ssl.keystore.password}")
    private String keystorePass;


    @Override
    public void configure() throws Exception {

        HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
        httpComponent.setSslContextParameters(SSLUtil.sslContextParameters(keystorePath, keystorePass));

        // Publish events via Kafka Bridge
        from("direct:produce-events").routeId("direct-produce-events")
                .setHeader(Exchange.CONTENT_TYPE, constant(KafkaConstants.KAFKA_PRODUCER_CONTENT_TYPE_JSON))
                .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.POST))
                .setHeader(Exchange.ACCEPT_CONTENT_TYPE, constant("application/vnd.kafka.v2+json"))
                .setHeader(KafkaConstants.GATEWAY_API_KEY, constant(gatewayApiKey))
                .setHeader("Host", constant("p2.uamp.hkt.com"))
                .toD("https4:" + kafkaBridgeBaseUrl + "${in.header.topic}"
                        + "?bridgeEndpoint=true"
                        + "&throwExceptionOnFailure=false"
                        + "&connectTimeout=30000"
                )
                .convertBodyTo(String.class)
                .log("${body}")
                .marshal().json()
                .end();

        // Subscribe events via Kafka Bridge
        from("direct:consume-events").routeId("direct-consume-events")
                .toD("kafka:ruby-topic?brokers=my-cluster-kafka-bootstrap-ruby-kafka-uat.app3.osp.pccw.com:443"+
	                "&sslTruststoreLocation=classpath:client-truststore.jks" +
                    "&sslTruststorePassword=password" +
                    "&securityProtocol=SSL" +
                    "&groupId=group1")
                .convertBodyTo(String.class)
                .log("${body}")
                .marshal().json()
                .end();
    }

}
