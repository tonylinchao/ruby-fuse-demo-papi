package com.hkt.ruby.fuse.demo.route;

import com.hkt.ruby.fuse.demo.constant.KafkaConstants;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Demo Component of Streaming File Transfer
 *
 * @author Tony C Lin
 */
@Component
public class FileRouter extends RouteBuilder {

    @Value("${mulesoft.file-stream-api}")
    private String muleFileStreamAPI;

	@Value("${appProxy.ip}")
	private String proxyServerIp;

	@Value("${appProxy.port}")
	private String proxyServerPort;

	@Override
	public void configure() throws Exception {
		
		// Call Mule API to get file in streaming
		from("direct:file-stream").routeId("direct-file-stream")
				.setHeader(KafkaConstants.HEADER_ACCEPT, constant(KafkaConstants.HEADER_CONTENT_TYPE_JSON))
				.toD("http4:" + muleFileStreamAPI + "${in.header.endpoint}" +"?fileName=${in.header.fileName}"
//						+ "&proxyAuthHost=" + proxyServerIp
//						+ "&proxyAuthPort=" + proxyServerPort
						+ "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true"
						+ "&readTimeout=300000")
				//.process(Processor)
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
