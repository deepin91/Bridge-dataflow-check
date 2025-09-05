package bridge.dto;

import lombok.Data;

@Data
public class ReportDto {
	
	private int reportIdx;
	private String reportReason;
	private String reportReasonDetail;
	private String userId;
	private String reportedUserId;

}
