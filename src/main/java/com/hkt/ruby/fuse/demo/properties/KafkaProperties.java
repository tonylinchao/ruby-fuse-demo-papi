package com.hkt.ruby.fuse.demo.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Kafka Properties
 *
 * @author Tony C Lin
 */
@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    @NestedConfigurationProperty
    private Bridge bridge;

    @NestedConfigurationProperty
    private Bootstrap bootstrap;

    @NestedConfigurationProperty
    private Gateway gateway;

    @NestedConfigurationProperty
    private Topic topic;

    @Getter
    @Setter
    public static class Bridge {
        private String baseUrl;
        private int subscribeTimeout;
        private int subscribeMaxBytes;
    }

    @Getter
    @Setter
    public static class Bootstrap {
        private String hostname;
        private int port;
        private String truststorePath;
        private String truststorePass;
        private String protocol;
    }

    @Getter
    @Setter
    public static class Gateway {
        private String hostname;
        private String apiKey;
    }

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private String partition;
        private String consumerGroup;
        private String consumerName;
    }
}
