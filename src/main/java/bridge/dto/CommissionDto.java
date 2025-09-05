package bridge.dto;

import lombok.Data;

@Data
public class CommissionDto {
	
	private int cIdx;
	private String userId1;
	private String userId2;
	private int cMoney;
	private int coMoney;

	private boolean deletedYn;
	private boolean progress;
	
}
