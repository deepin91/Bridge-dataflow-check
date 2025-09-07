package com.bridge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import bridge.dto.UserDto;
import bridge.mapper.LoginMapper;
import bridge.service.LoginService;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class BridgeServiceTest {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private LoginMapper loginMapper;
	
	@Test
	void testCreateAndGetUser() throws Exception {
		UserDto userDto = new UserDto();
		userDto.setUserId("tester2025");
		userDto.setUserPassword("tester2025!!");
		userDto.setUserName("tester2025");
		userDto.setUserEmail("tester2025@naver.com");
		userDto.setUserPhoneNumber("010-7777-7777");
		
		int result = loginService.registUser(userDto);
		assertEquals(1, result);
		
		UserDto saved = loginMapper.getloginDto(userDto);
		assertNotNull(saved);
		assertEquals("tester2025", saved.getUserId());
		assertEquals("tester2025@naver.com", saved.getUserEmail());
		
		UserDto mapperSaved = loginMapper.selectUserByUserId("tester2025");
        assertNotNull(mapperSaved);
        assertEquals("tester2025", mapperSaved.getUserId());
	}
}
// --0907 
// 위에 오류임 제대로 고치고 난 후에 돌려볼 것 
