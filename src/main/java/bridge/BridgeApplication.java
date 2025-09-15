package bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BridgeApplication.class, args);
	}
	
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	/*↑ 위 코드는 비밀번호 해시 암호화 시 시용되는 코드임 
	 * bridge.security패키지의 WebSecurity 파일으로 옮김 -- 9/15
	 */

}
