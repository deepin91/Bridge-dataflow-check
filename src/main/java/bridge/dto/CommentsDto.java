package bridge.dto;

import lombok.Data;

@Data
public class CommentsDto {
	private int ccIdx;
	private String ccComments;
	private String userId;
	private int cIdx;
	private String cDate;
	private String deletedYn;
	private int progress;
}
