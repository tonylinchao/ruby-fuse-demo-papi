package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.KafkaConstants;
import com.hkt.ruby.fuse.demo.utils.CommonUtils;
import com.hkt.ruby.fuse.demo.utils.Environments;
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

	@Value("${spring.profiles.active}")
	private String activeEnv;

    @Value("${mulesoft.api.test-vpc}")
    private String muleTestVPCAPI;

	@Value("${mulesoft.proxy}")
	private String muleProxy;

	@Value("${appProxy.host}")
	private String proxyServerHost;

	@Value("${appProxy.port}")
	private String proxyServerPort;

	@Value("${ssl.keystore.path}")
	private String keystorePath;

	@Value("${ssl.keystore.password}")
	private String keystorePass;

	@Override
	public void configure() throws Exception {

		String https4RequestUrl = null;

		if(Environments.DEV.getEnv().equals(activeEnv)){
			https4RequestUrl = "https4:" + muleTestVPCAPI + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		}else {
			https4RequestUrl = "https4:" + muleTestVPCAPI + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000"
					+ "&proxyAuthHost=" + proxyServerHost + "&proxyAuthPort=" + proxyServerPort;
		}

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		httpComponent.setSslContextParameters(CommonUtils.sslContextParameters(keystorePath, keystorePass));
		
		// Call Mule Exchange mock REST API to get customer info
		from("direct:test-vpc").routeId("direct-test-vpc")
				.setHeader(KafkaConstants.HEADER_ACCEPT, constant(KafkaConstants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(KafkaConstants.HEADER_HOST, constant(muleProxy))
				.setHeader("env", constant(activeEnv))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.log("${body}")
				//.process(customerProcessor)
				.marshal().json()
				.end();
	}

}
