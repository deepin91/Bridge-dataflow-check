package bridge.dto;

import lombok.Data;

@Data
public class PartnerPaymentDto {

	private int pdIdx;
	private String userId1; //보낸 사람 //로그인 사용자
	private String userId2; //받는 사람 //대상을 어디서 넘어가게 할 건지?
	private int pdMoney; //예치금
	
}
