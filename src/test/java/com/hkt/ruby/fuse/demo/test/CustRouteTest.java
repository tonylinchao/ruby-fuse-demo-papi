package com.hkt.ruby.fuse.demo.test;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class CustRouteTest {

    @EndpointInject(uri = "direct:test-vpc")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:log:sample-route")
    private MockEndpoint mockEndpoint;

    @Test
    public void testVpcAspect() throws InterruptedException {
        producerTemplate.sendBody("hello");

        mockEndpoint.assertIsSatisfied(0);
    }
}
