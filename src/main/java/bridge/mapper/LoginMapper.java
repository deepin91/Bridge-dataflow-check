package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.LoginDto;
import bridge.dto.UserDto;

@Mapper
public interface LoginMapper {
	public UserDto login(LoginDto loginDto) throws Exception;
	public int registUser(UserDto userDto) throws Exception;
	public UserDto selectUserByUserId(String userId);
	public UserDto getloginDto(UserDto userDto);
	//
	UserDto passInformation(UserDto usersDto) throws Exception;
	
	List<UserDto> selectUserId(UserDto usersDto) throws Exception;
	int userIdCheck(String userId) throws Exception;
	public String findId(String email);
	public UserDto findPassword(String email);
	public void updatePassword(UserDto userDto);
	//추가
	public int selectHalted(String username);
}
