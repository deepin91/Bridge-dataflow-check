package bridge.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import bridge.dto.ApproveResponseDto;
import bridge.dto.ReadyResponseDto;
import bridge.dto.UserDto;
import bridge.mapper.BridgeMapper;
import bridge.mapper.KakaoPayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoPayService {
	
	@Autowired
	private BridgeMapper bridgeMapper;
	@Autowired
	private KakaoPayMapper KakaoPayMapper;
	
	ReadyResponseDto readyResponse;	
	int totalAmount;
	String pod;
	String userId;
	// 카카오 요구 헤더 셋팅
		private HttpHeaders getHeaders() {
			HttpHeaders headers = new HttpHeaders();
		
			headers.set("Authorization", "KakaoAK c3f7d552fea955b0d2f2bf16cc4abacb"); 	//카카오 admin키
			headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			
			return headers;
		}
	
		//카카오페이 요청 양식
	public ReadyResponseDto payReady(int totalAmount,UserDto userDto) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		String uuid = UUID.randomUUID().toString();

		parameters.add("cid", "TC0ONETIME"); //가맹점 테스트코드
		parameters.add("partner_order_id",  uuid); //가맹점 주문 번호
		
		log.info(">>>>>>>>>>>>>>>>>>>>>" +  uuid);
		
		parameters.add("partner_user_id", userDto.getUserId()); //가맹점 회원 ID
		parameters.add("item_name", "포인트"); // 상품명
		parameters.add("quantity", "1"); // 주문 수량
		parameters.add("total_amount", String.valueOf(totalAmount)); //총 금액
		parameters.add("tax_free_amount", "0"); //상품 비과세 금액
		parameters.add("approval_url", "http://localhost:8080/order/pay/completed/{userId}"); // 결제승인시 넘어갈 url
		parameters.add("cancel_url", "http://localhost:8080/order/pay/cancel"); // 결제취소시 넘어갈 url
		parameters.add("fail_url", "http://localhost:8080/order/pay/fail"); // 결제 실패시 넘어갈 url
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
		
		RestTemplate template = new RestTemplate();
		
		String url = "https://kapi.kakao.com/v1/payment/ready";

		readyResponse = template.postForObject(url, requestEntity, ReadyResponseDto.class);
		log.info("결재준비 응답객체: " + readyResponse);
		pod = uuid;
		userId = userDto.getUserId();
		log.info("1111111111111111" + userDto.getUserId());
		this.totalAmount =  totalAmount;
		return readyResponse;
	}
	
	// 결제 승인요청 메서드  , String tid
		public ApproveResponseDto payApprove(String pgToken) {

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			parameters.add("cid","TC0ONETIME");
			parameters.add("tid", readyResponse.getTid());			
			parameters.add("partner_order_id", pod); // 주문명
			log.info("2222222222222" + userId);
			
			log.info("==========================" +pod);
			
			parameters.add("partner_user_id", userId);
			parameters.add("pg_token", pgToken);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
			
			RestTemplate template = new RestTemplate();
			String url = "https://kapi.kakao.com/v1/payment/approve";
			ApproveResponseDto approveResponse = template.postForObject(url, requestEntity, ApproveResponseDto.class);
			log.info("결재승인 응답객체: " + approveResponse);
			approveResponse.setMoney(totalAmount);
			KakaoPayMapper.insertPay(approveResponse);
			bridgeMapper.doCharge(approveResponse);
			
			return approveResponse;
		};

		
		

}
