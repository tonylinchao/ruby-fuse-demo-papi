package com.hkt.ruby.fuse.demo.test;

import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class CustRouteTest {

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    @Test
    public void testSendMatchingMessage() throws Exception {
        Exchange result = fluentProducerTemplate
                .to("direct:customer-info").send();
        result.getIn().getBody();
    }
}
