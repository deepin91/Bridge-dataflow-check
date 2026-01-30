package bridge.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import bridge.dto.LoginDto;
import bridge.dto.UserDto;
import bridge.mapper.LoginMapper;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginMapper loginMapper;
	// 해시값 설정
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public UserDto login(LoginDto loginDto) throws Exception {
		return loginMapper.login(loginDto);
	}
	
	
//  2025 수정 덧붙인 내용	
//	@Override
//	public UserDto login(LoginDto loginDto) throws Exception {
//		UserDto user = loginMapper.selectUserByUserId(loginDto.getUserId());
//
//		if (user == null) {
//			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
//		}
//
//		// 비밀번호 매칭 (암호화된 값 비교)
//		boolean match = passwordEncoder.matches(loginDto.getUserPassword(), user.getUserPassword());
//		if (!match) {
//			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//		}
//
//		return user;
//	}

	@Override
	public int registUser(UserDto userDto) throws Exception {
		// 패스워드를 암호화 해서 새로 저장
		userDto.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
		return loginMapper.registUser(userDto);
	}

	// String username, String password, boolean enabled,
	// boolean accountNonExpired, boolean credentialsNonExpired,
	// boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto userDto = loginMapper.selectUserByUserId(username);
		if (userDto == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(userDto.getUserId(), userDto.getUserPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getloginDto(UserDto userDto) {
		return loginMapper.getloginDto(userDto);
	}

	// 외부 로그인
	@Override
	public UserDto passInformation(UserDto userDto) throws Exception {
		return loginMapper.passInformation(userDto);
	}
	
	//2025잠시 수정
//	@Override
//	public UserDto passInformation(UserDto userDto) throws Exception {
//	    UserDto dbUser = loginMapper.selectUserByUserId(userDto.getUserId());
//	    if (dbUser != null && passwordEncoder.matches(userDto.getUserPassword(), dbUser.getUserPassword())) {
//	        return dbUser;
//	    }
//	    return null;
//	}
	//
//	@Override
	public UserDto passOrCreate(UserDto dto) throws Exception{
	    // 카카오 - 간편가입 + 로그인
	    if ("KAKAO".equalsIgnoreCase(dto.getProvider())) {
	    	
	    	UserDto found = loginMapper.selectUserByUserId(dto.getUserId());
	    	if(found != null) return found;
	    	// 카카오 최초 로그인 + 자동가입
	    	dto.setProvider("KAKAO");
	    	// -- 이메일, 핸드폰번호 null 허용으로 변경해서 가능
	    	
	    	loginMapper.insertSocialUser(dto);
	        return loginMapper.selectUserByUserId(dto.getUserId());
	    }

	    // 네이버 - 간편가입 + 로그인
	    if ("NAVER".equalsIgnoreCase(dto.getProvider())) {
	    	
	    	// 이메일로 기존 유저 조회
	    	UserDto found = loginMapper.passInformation(dto);
	    	if(found != null) return found;
		
	    	// 자동가입
	    	String newUserId = "naver_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
	    	dto.setUserId(newUserId);
	    	dto.setProvider("NAVER");
		
//	    	// ✅ ADD: phone 필수값 임시 세팅 (폰넘버 제공 ㄴㄴ라서)
//	    	if (dto.getUserPhoneNumber() == null || dto.getUserPhoneNumber().isBlank()) {
//	        	dto.setUserPhoneNumber("000-0000-0000");
//	    	}
		
	    	loginMapper.insertSocialUser(dto);
	    	
	    	return loginMapper.passInformation(dto);
	    }
	    
	    return null;
	}

	@Override
	public int userIdCheck(String userIdCheck) throws Exception {
		int result = loginMapper.userIdCheck(userIdCheck);
		return result;
	}

	@Override
	public String findId(String email) {
		// TODO Auto-generated method stub
		return loginMapper.findId(email);
	}

	@Override
	public void findPassword(String email,String password) {
		// TODO Auto-generated method stub
		UserDto userDto =  loginMapper.findPassword(email);
		userDto.setUserPassword(passwordEncoder.encode(password));
		loginMapper.updatePassword(userDto);		

	}
}
