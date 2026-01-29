package bridge.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import bridge.dto.LoginDto;
import bridge.dto.UserDto;

public interface LoginService extends UserDetailsService {
	public UserDto login(LoginDto loginDto) throws Exception;

	public int registUser(UserDto userDto) throws Exception;

	public UserDto getloginDto(UserDto userDto);

	// 외부 로그인
	public UserDto passInformation(UserDto userDto) throws Exception;
	//ID중복체크
	public int userIdCheck(String userId) throws Exception;

	public String findId(String email);

	public void findPassword(String email,String password);

	public UserDto passOrCreate(UserDto userDto) throws Exception;
}
