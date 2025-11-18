package bridge.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import bridge.interceptor.LoggerInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor());
		//registry.addInterceptor(new LoginCheckInterceptor());
	}
	@Override 
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")		
			.allowedMethods("*");

	}	
	
	// ✅ 이미지 정적 리소스 핸들러 추가
		@Override
		public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
			registry
				.addResourceHandler("/images/**") // 프론트에서 접근할 URL 경로
				.addResourceLocations("file:/app/files/images/"); // 실제 서버 내 저장 경로 (뒤에 `/` 꼭 붙이기)
		}
}
