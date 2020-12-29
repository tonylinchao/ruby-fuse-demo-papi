package com.hkt.ruby.fuse.demo.router;

import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Demo Component of Customer Routers
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
@EnableConfigurationProperties({MuleProperties.class, SystemProperties.class})
public class CustRouter extends RouteBuilder {

	@Autowired
	private MuleProperties muleProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Override
	public void configure() throws Exception {

		String https4RequestUrl = null;

		if(Constants.DEV.equals(systemProperties.getActiveEnv())){
			https4RequestUrl = "https4:" + muleProperties.getApi().getMockCustomers() + "${in.header.hkid}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		}else {
			https4RequestUrl = "https4:" + muleProperties.getApi().getMockCustomers() + "${in.header.hkid}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000"
					+ "&proxyAuthHost=" + systemProperties.getAppProxy().getHostname() + "&proxyAuthPort=" + systemProperties.getAppProxy().getPort();
		}

		// Call Mule Exchange mock REST API to get customer info
		from("direct:customers").routeId("direct-customers")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

		from("direct:customer-info").routeId("direct-customer-info")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD("https4:" + muleProperties.getApi().getCustomerInfo() + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000")
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

	}

}
