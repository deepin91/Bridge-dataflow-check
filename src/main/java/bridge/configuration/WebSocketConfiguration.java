package bridge.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@RequiredArgsConstructor
@Configuration // Spring 설정 파일
@EnableWebSocketMessageBroker //STOMP 브로커 활성화
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
//WebSocketMessageBrokerConfigurer를 구현
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) { // 메시지 구독(prefix: /sub), 발행(prefix: /pub) 설정
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) { // /ws 엔드포인트를 WebSocket으로 사용하도록 등록
		registry.addEndpoint("/ws").setAllowedOrigins("*"); // CORS 허용
	}
}
// 채팅관련