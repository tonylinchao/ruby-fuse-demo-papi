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
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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

		log.error("Request URI: " + https4RequestUrl);

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
		//ITAR hostname issue, skip hostname checking
		httpComponent = CommonUtils.skipHostnameCheck(httpComponent);
		
		// Get on-prem ITAR digiSPACE application list API
		from("direct:itar-digispace").routeId("direct-itar-digispace")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Constants.ITAR_DIGISPACE_SECRET, constant(onPremProperties.getSecret().getItarDigispace()))
				.toD(https4RequestUrl)
				.convertBodyTo(String.class)
				.marshal().json()
				.log("${body}")
				.end();

		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(); // 创建connectionFactory
		connectionFactory.setHostName("redis-ruby-fuse-uat.app3.osp.pccw.com");
		connectionFactory.setPort(80);
		connectionFactory.setDatabase(8);
		SimpleRegistry registry = new SimpleRegistry();
		connectionFactory.afterPropertiesSet(); // 必须要调用该方法来初始化connectionFactory
		registry.put("connectionFactory", connectionFactory); // 注册connectionFactory
		registry.put("serializer", new StringRedisSerializer()); // 注册serializer

		from("direct:itar-digispace-redis").routeId("direct-itar-digispace-redis")
				.setHeader(Constants.HEADER_ACCEPT, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.setHeader(Exchange.CONTENT_TYPE, constant(Constants.HEADER_CONTENT_TYPE_JSON))
				.convertBodyTo(String.class)
				.setHeader("CamelRedis.Command", constant("SET"))
				.setHeader("CamelRedis.Key", constant("keyOne"))
				.setHeader("CamelRedis.Value", constant("hello"))
				//spring-redis://simon-tls-redis-ruby-common-tools-sit.app3.osp.pccw.com?connectionFactory=#connectionFactory&serializer=#serializer
				.to("spring-redis://redis-ruby-fuse-uat.app3.osp.pccw.com")
				.log("${body}")
				.marshal().json()
				.end();

	}



}
