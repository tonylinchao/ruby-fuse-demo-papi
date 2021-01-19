package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
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
public class CustRoute extends RouteBuilder {

	@Autowired
	private MuleProperties muleProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Override
	public void configure() throws Exception {

		String https4RequestUrlOfCustomers = "https4:" + muleProperties.getApi().getMockCustomers()
				+ "${in.header.hkid}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		String https4RequestUrlOfCustomerInfo = "https4:" + muleProperties.getApi().getCustomerInfo()
				+ "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";
		String https4RequestUrlOfSalesforceContacts = "https4:" + muleProperties.getApi().getSalesforceContacts()
				+ "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout=30000";

		if(!Constants.DEV.equals(systemProperties.getActiveEnv())){
			https4RequestUrlOfCustomers = systemProperties.getSystemProxy(https4RequestUrlOfCustomers);
			https4RequestUrlOfCustomerInfo = systemProperties.getSystemProxy(https4RequestUrlOfCustomerInfo);
			https4RequestUrlOfSalesforceContacts = systemProperties.getSystemProxy(https4RequestUrlOfSalesforceContacts);
		}

		// Default error handling
		onException(Exception.class)
				.log("Error info: ${exception}");

		// Http request retry
		onException(HttpOperationFailedException.class)
				.log("Http request failed: ${exception}")
				.maximumRedeliveries(3)
				.maximumRedeliveryDelay(1000)
				.retryAttemptedLogLevel(LoggingLevel.INFO);

		// Call Mule Exchange mock REST API to get customer info
		from("direct:customers").routeId("direct-customers")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrlOfCustomers)
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

		// Call Mule API to get customer info
		from("direct:customer-info").routeId("direct-customer-info")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrlOfCustomerInfo)
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

		// Get Salesforce contacts via Mule
		from("direct:salesforce-contacts").routeId("direct-salesforce-contacts")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(https4RequestUrlOfSalesforceContacts)
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

	}

}
