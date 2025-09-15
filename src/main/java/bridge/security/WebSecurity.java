package bridge.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import bridge.mapper.LoginMapper;
import bridge.service.LoginService;

@Configuration
public class WebSecurity {


    private final LoginMapper loginMapper;
    private final Environment env;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurity(LoginMapper loginMapper,
                       Environment env,
                       JwtTokenUtil jwtTokenUtil,
                       JwtRequestFilter jwtRequestFilter) {
    	this.loginMapper = loginMapper;
        this.env = env;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtRequestFilter = jwtRequestFilter;
    }
    
    // Spring Security 6 방식
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager) throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(loginMapper, env, jwtTokenUtil);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/login");

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilter(authenticationFilter)
            .cors(cors -> {});

        return http.build();
    }
    
    // AuthenticationManager 주입 방식 변경됨
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // DaoAuthenticationProvider 사용
    // Bean 내부 파라미터로 LoginService와 Encoder 받기 -- 순환참조 이슈로 무한루프에 빠짐 -해결 위해 필드 선언부에서 LoginService 삭제 및 BCryptPasswordEncoder 삭제 > 파라미터로 주입받아서 사용 
    @Bean
    public AuthenticationProvider authenticationProvider(LoginService loginService,
    													 BCryptPasswordEncoder passwordEncoder) { // -- 테스트 시 순환참조 오류로인한 수정 - LoginService는 파라미터로 주입받아서 사용 (순환참조 회피) 
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(loginService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception{
//		http.csrf().disable();
////		http.authorizeRequests().antMatchers("**").permitAll();
////		http.authorizeRequests()
////		.antMatchers("/api/board/**").authenticated()
////		.and().addFilter(getAuthenticationFilter());
////	
//		http.authorizeRequests()
//			.antMatchers("**").permitAll()	
//		.anyRequest().authenticated()
//		.and().addFilter(getAuthenticationFilter())
//		.addFilterBefore(jwtRequestFilter, AuthenticationFilter.class)
//		.cors();
//}
//
//
//	private AuthenticationFilter getAuthenticationFilter()throws Exception{
//		AuthenticationFilter authenticationFilter = new AuthenticationFilter(loginMapper,env,jwtTokenUtil);
//		authenticationFilter.setAuthenticationManager(authenticationManager());
//		return authenticationFilter;
//	}
//	//인증 처리 방법을 설정
//	//사용자 정보를 조회할 서비스와 패스워드 암호화에 사용할 방식을 지정
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(loginService).passwordEncoder(passwordEncoder);
//	}
//}