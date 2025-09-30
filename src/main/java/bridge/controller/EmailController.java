package bridge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bridge.service.EmailService;
import lombok.extern.slf4j.Slf4j;

/**
 * 이메일 컨트롤러
 */

@Slf4j
@RestController
public class EmailController {

	@Autowired
	EmailService emailService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@PostMapping("/emailConfirm/{email}")
	public ResponseEntity<String> emailConfirm(@PathVariable("email") String email) {
		try {
			emailService.sendSimpleMessage(email);
			return ResponseEntity.ok("인증메일이 성공적으로 전송되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송에 실패했습니다. 입력 내용을 다시 확인해주세요");
		}
//    	log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+email);
//      String confirm = emailService.sendSimpleMessage(email);
//      return confirm;
	}

	@PostMapping("/verifyCode")
	public ResponseEntity<String> verifyCode(@RequestParam("email") String email, 
											@RequestParam("code") String code) {
		String savedCode = redisTemplate.opsForValue().get(email);

		if (savedCode != null && savedCode.equals(code)) {
			redisTemplate.delete(email);
			return ResponseEntity.ok("이메일 인증 성공");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패: 코드가 일치하지 않거나 만료되었습니다. 다시 확인해주세요. ");
		}
	}
}