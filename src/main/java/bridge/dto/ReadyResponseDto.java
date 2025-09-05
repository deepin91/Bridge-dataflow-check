package bridge.dto;

import lombok.Data;

@Data
//결제요청 dto
public class ReadyResponseDto {
	
	
	private String tid; //결제고유 번호
	private String next_redirect_pc_url; //pc버전 받는 결제페이지 
	private String partner_order_id; 
	private String created_at;
}
