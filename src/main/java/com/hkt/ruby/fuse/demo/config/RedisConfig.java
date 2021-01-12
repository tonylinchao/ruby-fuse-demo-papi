package com.hkt.ruby.fuse.demo.config;

import com.hkt.ruby.fuse.demo.properties.MuleProperties;
import com.hkt.ruby.fuse.demo.properties.SystemProperties;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *  Redis connection configuration
 *
 * @author Tony C Lin
 */
@Configuration
@EnableConfigurationProperties(SystemProperties.class)
public class RedisConfig {

    @Autowired
    private SystemProperties systemProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(systemProperties.getRedis().getHostname());
        jedisConnectionFactory.setPort(systemProperties.getRedis().getPort());
        jedisConnectionFactory.setPassword(systemProperties.getRedis().getPassword());

        return jedisConnectionFactory;
    }

    @Bean
    public SimpleRegistry registry() {
        SimpleRegistry registry = new SimpleRegistry();
        jedisConnectionFactory().afterPropertiesSet(); // 必须要调用该方法来初始化connectionFactory
        registry.put("connectionFactory", jedisConnectionFactory()); // 注册connectionFactory
        registry.put("serializer", new StringRedisSerializer()); // 注册serializer
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
