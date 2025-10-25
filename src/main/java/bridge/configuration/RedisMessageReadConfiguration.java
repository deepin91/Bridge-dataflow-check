package bridge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisMessageReadConfiguration {

	@Bean
	public RedisTemplate<String, Integer> RedisMessageReadConfiguration(RedisConnectionFactory connectionFactory){
		RedisTemplate<String, Integer> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer()); 
		template.setValueSerializer(new GenericToStringSerializer<>(Integer.class)); // 숫자 직렬화
		return template;
	}
}
