package com.hkt.ruby.fuse.demo.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * System Properties
 *
 * @author Tony C Lin
 */
@Data
@Component
@ConfigurationProperties(prefix = "")
public class SystemProperties {

    @NestedConfigurationProperty
    private SSL ssl;

    @NestedConfigurationProperty
    private AppProxy appProxy;

    @Value("${spring.profiles.active}")
    private String activeEnv;

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "ssl")
    public static class SSL {
        private String truststorePath;
        private String truststorePass;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "app-proxy")
    public static class AppProxy {
        private String hostname;
        private int port;
    }
}
