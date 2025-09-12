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

    private final LoginService loginService;
    private final LoginMapper loginMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurity(LoginService loginService,
                       BCryptPasswordEncoder passwordEncoder,
                       LoginMapper loginMapper,
                       Environment env,
                       JwtTokenUtil jwtTokenUtil,
                       JwtRequestFilter jwtRequestFilter) {
        this.loginService = loginService;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.loginMapper = loginMapper;
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
    @Bean
    public AuthenticationProvider authenticationProvider() {
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