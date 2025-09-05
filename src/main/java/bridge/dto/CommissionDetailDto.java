package bridge.dto;

import lombok.Data;

@Data
public class CommissionDetailDto {
	
	private int cdIdx;
	private String cdComment;
	private String cdDate;
	private String userId;
	private int cIdx;
	private String cdFile;
	private int deletedYn;

}
