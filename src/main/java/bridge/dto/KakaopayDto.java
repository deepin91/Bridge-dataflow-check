package bridge.dto;

import lombok.Data;

@Data
public class KakaopayDto {
	
	private String userId;
	private int kakaopayIdx;
	private String pgToken;
	private String tid;

}
