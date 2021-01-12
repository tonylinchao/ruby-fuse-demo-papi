package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.OnPremProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import com.hkt.ruby.fuse.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Get ITAR digiSPACE app list
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
@EnableConfigurationProperties({OnPremProperties.class, SystemProperties.class})
public class ItarRoute extends RouteBuilder {

	@Autowired
	private OnPremProperties onPremProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Override
	public void configure() throws Exception {

		String https4RequestUrl = "https4:" + onPremProperties.getApi().getItarDigispace() + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";

//		if(!Constants.DEV.equals(systemProperties.getActiveEnv())){
//			https4RequestUrl = systemProperties.getSystemProxy(https4RequestUrl);
//		}
		log.debug("Request URI: " + https4RequestUrl);

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		httpComponent.setSslContextParameters(CommonUtils.sslContextParameters(systemProperties.getSsl().getTruststorePath(),
				systemProperties.getSsl().getTruststorePass()));
		
		// Get on-prem ITAR digiSPACE application list API
		from("direct:itar-digispace").routeId("direct-itar-digispace")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();
	}

}
