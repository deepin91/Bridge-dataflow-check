package bridge.dto;

import lombok.Data;

@Data
public class KakaopayListDto {
	
	private int kakaopayIdx;
	private String userId;
	private String tid;
	private String partnerOrderId;
	private String createdAt;

}
