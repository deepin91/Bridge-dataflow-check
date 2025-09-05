package bridge.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.ApproveResponseDto;
import bridge.dto.ReadyResponseDto;
import bridge.dto.UserDto;
import bridge.mapper.KakaoPayMapper;
import bridge.service.KakaoPayService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class KakaoPayController {

	@Autowired
	private KakaoPayService kakaopayService;
	
	@Autowired
	private KakaoPayMapper kakaoPayMapper;
	
	@ApiOperation(value="카카오페이 결제 요청")
	@GetMapping("/order/pay/{totalAmount}/{userId}")
	public @ResponseBody ReadyResponseDto payReady(@PathVariable("totalAmount") int totalAmount, Model model, @PathVariable("userId") String userId) {
		UserDto userDto = new UserDto();
		userDto.setUserId(userId);
		ReadyResponseDto readyResponse = kakaopayService.payReady(totalAmount,userDto);
		model.addAttribute("tid", readyResponse.getTid());
		model.addAttribute("partner_order_id", readyResponse.getPartner_order_id());

		log.info("11111111111결재고유 번호: " + readyResponse.getTid());
		log.info("22222222222파트너: " + readyResponse.getPartner_order_id());
		log.info("33333333333333"+readyResponse.getCreated_at());
		
		return readyResponse;
	}


	@ApiOperation(value="카카오페이 결제 완료")
	@GetMapping("/order/pay/completed/{userId}")
	public void payCompleted(@RequestParam("pg_token") String pgToken, HttpServletResponse response, @PathVariable("userId") String userId) throws IOException{
		log.info("22222222222222결제승인 요청을 인증하는 토큰: " + pgToken);

		ApproveResponseDto approveResponse = kakaopayService.payApprove(pgToken);
		kakaoPayMapper.kakaoToList();
		System.out.println(">>>>>>>>>>>>>>>>>>" + approveResponse);
		
		log.info("33333333333333" + userId);
	
		response.sendRedirect("http://localhost:3000/");
	}

	@ApiOperation(value="카카오페이 결제 취소")
	@GetMapping("/order/pay/cancel")
	public String payCancel() {
		return "결제취소";
	}

	@ApiOperation(value="카카오페이 결제 실패")
	@GetMapping("/order/pay/fail")
	public String payFail() {
		return "redirect:/order/pay";
	}
	
}
