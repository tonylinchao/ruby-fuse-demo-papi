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
@ConfigurationProperties(prefix = "system")
public class SystemProperties {

    @NestedConfigurationProperty
    private SSL ssl;

    @NestedConfigurationProperty
    private AppProxy appProxy;

    @NestedConfigurationProperty
    private Redis redis;

    @Value("${spring.profiles.active}")
    private String activeEnv;

    @Getter
    @Setter
    public static class SSL {
        private String keystorePath;
        private String keystorePass;
        private String truststorePath;
        private String truststorePass;
    }

    @Getter
    @Setter
    public static class AppProxy {
        private String hostname;
        private int port;
        private String scheme;
    }

    @Getter
    @Setter
    public static class Redis {
        private String hostname;
        private int port;
        private String password;
    }

    public String getSystemProxy (String https4RequestUrl) {
        return https4RequestUrl + "&proxyAuthHost=" + appProxy.hostname
                + "&proxyAuthPort=" + appProxy.port
                + "&proxyAuthScheme=" + appProxy.scheme;
    }
}
