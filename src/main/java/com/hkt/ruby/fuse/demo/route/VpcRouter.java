package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.utils.SSLUtil;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Test VPC
 *
 * @author Tony C Lin
 */
@Component
public class VpcRouter extends RouteBuilder {

    @Value("${mulesoft.test-vpc}")
    private String muleTestVPCAPI;

	@Value("${appProxy.ip}")
	private String proxyServerIp;

	@Value("${appProxy.port}")
	private String proxyServerPort;

	@Value("${ssl.keystore.path}")
	private String keystorePath;

	@Value("${ssl.keystore.password}")
	private String keystorePass;

	@Override
	public void configure() throws Exception {

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		httpComponent.setSslContextParameters(SSLUtil.sslContextParameters(keystorePath, keystorePass));
		
		// Call Mule Exchange mock REST API to get customer info
		from("direct:test-vpc").routeId("direct-test-vpc")
				.setHeader("Accept", constant("application/json"))
				.setHeader("Host", constant("np1.muleamp.hkt.com"))
				.toD("https4:" + muleTestVPCAPI
						+ "?proxyAuthHost=" + proxyServerIp
						+ "&proxyAuthPort=" + proxyServerPort
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

}
