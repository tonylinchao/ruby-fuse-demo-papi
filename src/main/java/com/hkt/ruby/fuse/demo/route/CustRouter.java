package com.hkt.ruby.fuse.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * Demo Component of Customer Routers
 *
 * @author Tony C Lin
 */
@Component
public class CustRouter extends RouteBuilder {

    @Value("${mockapi.customers-api}")
    private String muleMockCustAPI;

	@Override
	public void configure() throws Exception {
		
		// Call Mule Exchange mock REST API to get customer info
		from("direct:customers").routeId("direct-customers")
				.setHeader("Accept", constant("application/json"))
				.toD("https4:" + muleMockCustAPI + "${in.header.hkid}"
						+ "?bridgeEndpoint=true"
						+ "&throwExceptionOnFailure=false"
						+ "&connectTimeout=30000")
				.convertBodyTo(String.class)
				.log("${body}")
				//.process(customerProcessor)
				.marshal().json()
				.end();
	}

}
