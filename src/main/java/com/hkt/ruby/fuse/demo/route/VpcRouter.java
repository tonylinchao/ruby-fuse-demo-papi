package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import com.hkt.ruby.fuse.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Test VPC
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
@EnableConfigurationProperties({MuleProperties.class, SystemProperties.class})
public class VpcRouter extends RouteBuilder {

	@Autowired
	private MuleProperties muleProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Override
	public void configure() throws Exception {

		String https4RequestUrl = null;

		if(Constants.DEV.equals(systemProperties.getActiveEnv())){
			https4RequestUrl = "https4:" + muleProperties.getApi().getTestVpc() + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		}else {
			https4RequestUrl = "https4:" + muleProperties.getApi().getTestVpc() + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000"
					+ "&proxyAuthHost=" + systemProperties.getAppProxy().getHostname() + "&proxyAuthPort=" + systemProperties.getAppProxy().getPort();
		}

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		httpComponent.setSslContextParameters(CommonUtils.sslContextParameters(systemProperties.getSsl().getTruststorePath(),
				systemProperties.getSsl().getTruststorePass()));
		
		// Call Mule Exchange mock REST API to get customer info
		from("direct:test-vpc").routeId("direct-test-vpc")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Constants.HEADER_HOST, constant(muleProperties.getProxy()))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.log("${body}")
				//.process(customerProcessor)
				.marshal().json()
				.end();
	}

}
