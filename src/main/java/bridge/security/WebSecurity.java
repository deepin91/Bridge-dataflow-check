package bridge.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import bridge.mapper.LoginMapper;
import bridge.service.LoginService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private LoginService loginService;
	private LoginMapper loginMapper;
	private BCryptPasswordEncoder passwordEncoder;
	private Environment env;
	private JwtTokenUtil jwtTokenUtil;
	private JwtRequestFilter jwtRequestFilter;

	public WebSecurity( LoginService loginService,BCryptPasswordEncoder passwordEncoder,
			LoginMapper loginMapper, Environment env,JwtTokenUtil jwtTokenUtil
			,JwtRequestFilter jwtRequestFilter){
		this.loginService = loginService;
		this.passwordEncoder = passwordEncoder;
		this.env = env;
		this.loginMapper = loginMapper;
		this.jwtTokenUtil = jwtTokenUtil;
		this.jwtRequestFilter = jwtRequestFilter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable();
//		http.authorizeRequests().antMatchers("**").permitAll();
//		http.authorizeRequests()
//		.antMatchers("/api/board/**").authenticated()
//		.and().addFilter(getAuthenticationFilter());
//	
		http.authorizeRequests()
			.antMatchers("**").permitAll()	
		.anyRequest().authenticated()
		.and().addFilter(getAuthenticationFilter())
		.addFilterBefore(jwtRequestFilter, AuthenticationFilter.class)
		.cors();
}


	private AuthenticationFilter getAuthenticationFilter()throws Exception{
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(loginMapper,env,jwtTokenUtil);
		authenticationFilter.setAuthenticationManager(authenticationManager());
		return authenticationFilter;
	}
	//인증 처리 방법을 설정
	//사용자 정보를 조회할 서비스와 패스워드 암호화에 사용할 방식을 지정
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(loginService).passwordEncoder(passwordEncoder);
	}
}