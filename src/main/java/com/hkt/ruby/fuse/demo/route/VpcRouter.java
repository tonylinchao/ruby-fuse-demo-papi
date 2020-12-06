package com.hkt.ruby.fuse.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
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

	@Override
	public void configure() throws Exception {


		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		httpComponent.setSslContextParameters(mySslContextParameters());

		
		// Call Mule Exchange mock REST API to get customer info
		from("direct:test-vpc").routeId("direct-test-vpc")
				.setHeader("Accept", constant("application/json"))
				.setHeader("Host", constant("np1.muleamp.hkt.com"))
				.toD("https4:" + muleTestVPCAPI
//						+ "?proxyAuthHost=" + proxyServerIp
//						+ "&proxyAuthPort=" + proxyServerPort
						+ "?bridgeEndpoint=true"
						+ "&throwExceptionOnFailure=false"
						+ "&connectTimeout=30000"
						+ "&sslContextParameters=#mySslContextParameters"
				)
				.convertBodyTo(String.class)
				.log("${body}")
				//.process(customerProcessor)
				.marshal().json()
				.end();
	}

	private SSLContextParameters mySslContextParameters(){

		KeyStoreParameters store = new KeyStoreParameters();
		store.setResource("classpath:mule-hkt.p12");
		store.setPassword("changeit");

		KeyManagersParameters key = new KeyManagersParameters();
		key.setKeyPassword("");
		key.setKeyStore(store);

		TrustManagersParameters trust = new TrustManagersParameters();
		trust.setKeyStore(store);

		SSLContextParameters parameters = new SSLContextParameters();
		parameters.setTrustManagers(trust);
		parameters.setKeyManagers(key);

		return parameters;
	}



}
