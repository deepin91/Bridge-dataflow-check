package com.bridge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BridgeApplicationTest {

	@Test
	void contestLoads() {
		// 이 메서드가 비어 있어도 Spring Boot가 컨텍스트를 정상적으로 띄우면 테스트 통과됨
        // 실패하면 설정 문제나 Bean 주입 문제가 있다는 의미
		// smoke test - 프로젝트 전체가 잘 기동되는지만 확인하는 용도
	}
}


/*
 * 1. Repository / Mapper test -> DB 체크 / 쿼리확인
 * 2. Service test - 레포지토리+ 비지니스 로직 조합(로그인, 회원가입 암호화 등)
 * 3. Controller test - 맨 마지막. 검증 다 끝내고 MockMvc 같은 걸로 API 요청 (GET /users/1) → 응답(200 OK, JSON body)을 확인
 * (여기서 에러가 나면 거의 90%는 URL 매핑, Request/Response DTO, 시큐리티 설정 문제)
 */