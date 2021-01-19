package com.hkt.ruby.fuse.demo.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "on-prem")
public class OnPremProperties {
    @NestedConfigurationProperty
    private API api;

    @NestedConfigurationProperty
    private Secret secret;

    @NestedConfigurationProperty
    private File file;

    @Getter
    @Setter
    public static class API {
        private String itarDigispace;
    }

    @Getter
    @Setter
    public static class Secret {
        private String itarDigispace;
    }

    @Getter
    @Setter
    public static class File {
        private String largeFile;
    }
}
