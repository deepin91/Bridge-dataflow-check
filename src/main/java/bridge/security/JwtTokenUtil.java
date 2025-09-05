package bridge.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import bridge.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenUtil {

	private String secret;
	private Long expirationTime;
	private Key hmacKey;

	public JwtTokenUtil(Environment env) {
		this.secret = env.getProperty("token.secret");
		this.expirationTime = Long.parseLong(env.getProperty("token.expiration-time"));
		this.hmacKey = new SecretKeySpec(Base64.getDecoder().decode(this.secret),
				SignatureAlgorithm.HS256.getJcaName());
		log.debug(this.secret);
		log.debug(Long.toString(this.expirationTime));
	}

	public String generateToken(UserDto userDto) {
		Instant now = Instant.now();
		String jwtToken = Jwts.builder().claim("name", userDto.getUserName()).claim("email", userDto.getUserEmail())
				.setSubject(userDto.getUserId())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(this.expirationTime, ChronoUnit.MILLIS)))
				.signWith(this.hmacKey)
				.compact();
		log.debug("**************************************");
		log.debug(jwtToken);
		log.debug("**************************************");
		return jwtToken;
	}

	private Claims getAllClaimsFromToken(String token) {
		Jws<Claims> jwt = Jwts.parserBuilder()
				.setSigningKey(this.hmacKey).build()
				.parseClaimsJws(token);
		return jwt.getBody();
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public String getSubjectFromToken(String token) {
		return getClaimFromToken(token,Claims::getSubject);
	}
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token,Claims::getExpiration);
	}
	
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	public Boolean validateToken(String token,UserDto userDto) {
		if(isTokenExpired(token)) {
			return Boolean.FALSE;
		}
		
		//토큰 내용을 검증
		String subject = getSubjectFromToken(token);	
		String userId = userDto.getUserId();
		
		if(subject != null && userId != null && subject.equals(userId)) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}
}
