package bridge.dto;

import lombok.Data;

@Data
//결제승인 요청 dto
public class ApproveResponseDto {
	private String aid; // 요청 고유 번호
	private String tid; // 결제 고유 번호
	private String cid; // 가맹점 코드
	private String sid; // 정기 결제용 ID
	private String partner_order_id; // 가맹점 주문 번호
	private String partner_user_id; // 가맹점 회원 ID
	private String payment_method_type; // 결제 수단
	private String item_name; // 상품명
	private String item_code; // 상품 코드
	private int quantity; // 상품수량
	private String created_at; // 결제 요청 시간
	private String approved_at; // 결제 승인 시간
	private String payload; // 결제 승인요청에 대한 저장값, 요청시 전달 내용
	private int money;
//	private Amount amount;
//	private String userId;
}
