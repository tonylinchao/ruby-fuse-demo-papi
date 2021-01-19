package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.Constants;
import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.OnPremProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Demo Component of Streaming File Transfer
 *
 * @author Tony C Lin
 */
@Slf4j
@Component
@EnableConfigurationProperties({MuleProperties.class, SystemProperties.class, OnPremProperties.class})
public class FileRoute extends RouteBuilder {

	@Autowired
	private MuleProperties muleProperties;

	@Autowired
	private SystemProperties systemProperties;

	@Autowired
	private OnPremProperties onPremProperties;

	@Override
	public void configure() throws Exception {

		String http4RequestUrl = "http4:" + muleProperties.getApi().getFileStream() + "${in.header.endpoint}?fileName=${in.header.fileName}"
				+ "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true&readTimeout=300000";

		if(!Constants.DEV.equals(systemProperties.getActiveEnv())){
			http4RequestUrl = systemProperties.getSystemProxy(http4RequestUrl);
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

		// Test large file tranfer
		from("direct:large-file").routeId("direct-large-file")
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						log.info("File path: " + onPremProperties.getFile().getLargeFile());
						Resource resource = new ClassPathResource(onPremProperties.getFile().getLargeFile());
						InputStream inputStream = resource.getInputStream();
						String data = "";
						try {
							byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
							data = new String(bdata, StandardCharsets.UTF_8);
							log.info("Retrieve data success!");
						} catch (IOException e) {
							log.error("IOException", e);
						}
						exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ResultCode.SUCCESS.getCode());
						exchange.getIn().setBody(data);
					}
				})
				.convertBodyTo(String.class)
				.log("${body}")
				.marshal().json()
				.end();

	}



}
