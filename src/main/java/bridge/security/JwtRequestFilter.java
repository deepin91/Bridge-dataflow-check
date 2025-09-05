package bridge.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import bridge.dto.UserDto;
import bridge.mapper.LoginMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
   
//	@Autowired
   private JwtTokenUtil jwtTokenUtil; 
   private LoginMapper loginMapper;
   
   public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, LoginMapper loginMapper) {
      this.jwtTokenUtil = jwtTokenUtil;
      this.loginMapper = loginMapper;
   }
   
   @Override
   public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {
   
      String jwtToken = null;
      String subject = null;
      String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
         jwtToken = authorizationHeader.substring(7);
         subject = jwtTokenUtil.getSubjectFromToken(jwtToken);
      } else {
         log.error("Authoriztion 헤더 누락 또는 토큰 형식 오류");
      }

      if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
         UserDto userDto = loginMapper.selectUserByUserId(subject);
         
         if (jwtTokenUtil.validateToken(jwtToken, userDto)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
               = new UsernamePasswordAuthenticationToken(userDto, null, null);
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
         }
      } else {
         SecurityContextHolder.getContext().setAuthentication(null);
      }
      
      filterChain.doFilter(request, response);
   }   
}