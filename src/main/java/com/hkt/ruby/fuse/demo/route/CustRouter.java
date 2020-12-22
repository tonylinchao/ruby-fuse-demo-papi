package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.KafkaConstants;
import com.hkt.ruby.fuse.demo.utils.SSLUtil;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
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

	@Value("${appProxy.ip}")
	private String proxyServerIp;

	@Value("${appProxy.port}")
	private String proxyServerPort;

	@Override
	public void configure() throws Exception {

		// Call Mule Exchange mock REST API to get customer info
		from("direct:customers").routeId("direct-customers")
				.setHeader(KafkaConstants.HEADER_ACCEPT, constant(KafkaConstants.HEADER_CONTENT_TYPE_JSON))
				.toD("https4:" + muleMockCustAPI + "${in.header.hkid}"
//						+ "?proxyAuthHost=" + proxyServerIp
//						+ "&proxyAuthPort=" + proxyServerPort
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
