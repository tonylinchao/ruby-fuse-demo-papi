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
 * Demo Component of Streaming File Transfer
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
@EnableConfigurationProperties({MuleProperties.class, SystemProperties.class})
public class FileRouter extends RouteBuilder {

	@Autowired
	private MuleProperties muleProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Override
	public void configure() throws Exception {

		String http4RequestUrl = null;

		if(Constants.DEV.equals(systemProperties.getActiveEnv())){
			http4RequestUrl = "http4:" + muleProperties.getApi().getFileStream() + "${in.header.endpoint}?fileName=${in.header.fileName}"
					+ "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true&readTimeout=300000";
		}else {
			http4RequestUrl = "http4:" + muleProperties.getApi().getFileStream() + "${in.header.endpoint}?fileName=${in.header.fileName}"
					+ "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true&readTimeout=300000"
					+ "&proxyAuthHost=" + systemProperties.getAppProxy().getHostname() + "&proxyAuthPort=" + systemProperties.getAppProxy().getPort();
		}
		
		// Call Mule API to get file in streaming
		from("direct:file-stream").routeId("direct-file-stream")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.toD(http4RequestUrl)
				.convertBodyTo(String.class)
				.choice()
					.when(header("outputFile").isNotNull())
						.to("direct:file-save")
					.otherwise()
					.log("${body}")
					.marshal().json()
				.end();


		// Save file
		from("direct:file-save").routeId("direct-file-save")
				.to("file:output?fileName=${in.header.outputFile}&charset=utf-8")
				.end();
	}

}
