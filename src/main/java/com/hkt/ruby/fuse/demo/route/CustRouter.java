package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.KafkaConstants;
import com.hkt.ruby.fuse.demo.utils.Environments;
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

	@Value("${spring.profiles.active}")
	private String activeEnv;

    @Value("${mock.api.customers}")
    private String muleMockCustAPI;

	@Value("${appProxy.host}")
	private String proxyServerHost;

	@Value("${appProxy.port}")
	private String proxyServerPort;

	@Override
	public void configure() throws Exception {

		String https4RequestUrl = null;

		if(Environments.DEV.getEnv().equals(activeEnv)){
			https4RequestUrl = "https4:" + muleMockCustAPI + "${in.header.hkid}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		}else {
			https4RequestUrl = "https4:" + muleMockCustAPI + "${in.header.hkid}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000"
					+ "&proxyAuthHost=" + proxyServerHost + "&proxyAuthPort=" + proxyServerPort;
		}

		// Call Mule Exchange mock REST API to get customer info
		from("direct:customers").routeId("direct-customers")
				.setHeader(KafkaConstants.HEADER_ACCEPT, constant(KafkaConstants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.log("${body}")
				//.process(customerProcessor)
				.marshal().json()
				.end();
	}

}
