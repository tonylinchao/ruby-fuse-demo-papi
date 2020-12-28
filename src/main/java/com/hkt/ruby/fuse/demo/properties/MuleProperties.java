package com.hkt.ruby.fuse.demo.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MuleSoft Properties
 *
 * @author Tony C Lin
 */
@Data
@ConfigurationProperties(prefix = "mule")
public class MuleProperties {

    @NestedConfigurationProperty
    private API api;

    private String proxy;

    @Getter
    @Setter
    public static class API {
        private String mockCustomers;
        private String fileStream;
        private String testVpc;
        private String customerInfo;
    }
}
