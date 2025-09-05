//package bridge.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//@Configuration
//public class RedisConfig {
//
//	@Bean
//	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//		RedisTemplate<String, String> template = new RedisTemplate<>();
//		template.setConnectionFactory(connectionFactory);
//		template.setKeySerializer(new StringRedisSerializer()); // 🔧 키 직렬화
//		template.setValueSerializer(new StringRedisSerializer()); // 🔧 값 직렬화
//		return template;
//	}
//
//}
