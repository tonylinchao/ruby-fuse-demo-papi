package com.hkt.ruby.fuse.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Value;

public class KafkaRouter extends RouteBuilder {

    @Value("${kafka.bridge.baseurl}")
    private String kafkaBridgeBaseUrl;

    @Override
    public void configure() throws Exception {

        HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
        httpComponent.setSslContextParameters(kafkaSslContextParameters());

        // Publish event via Kafka Bridge
        from("direct:produce-event").routeId("direct-produce-event")
                .setHeader("Content-Type", constant("application/vnd.kafka.json.v2+json"))
                .setHeader("CamelHttpMethod", constant("POST"))
                .toD("http4:" + kafkaBridgeBaseUrl + "/topics/" + "${in.header.topic}"
                        + "&bridgeEndpoint=true"
                        + "&throwExceptionOnFailure=false"
                        + "&connectTimeout=30000"
                )
                .convertBodyTo(String.class)
                .log("${body}")
                //.process(customerProcessor)
                .marshal().json()
                .end();
    }


    private SSLContextParameters kafkaSslContextParameters(){

        KeyStoreParameters store = new KeyStoreParameters();
        store.setResource("classpath:local-truststore.jks");
        store.setPassword("password");

        KeyManagersParameters key = new KeyManagersParameters();
        key.setKeyPassword("");
        key.setKeyStore(store);

        TrustManagersParameters trust = new TrustManagersParameters();
        trust.setKeyStore(store);

        SSLContextParameters parameters = new SSLContextParameters();
        parameters.setTrustManagers(trust);
        parameters.setKeyManagers(key);

        return parameters;
    }

}
