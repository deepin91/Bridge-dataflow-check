package bridge.dto;

import lombok.Data;

@Data
public class CommissionCommentDto {

	private int ccIdx;
	private String userId;
	private String ccDate;
	private String ccContents;
	private int cdIdx;
	
}
