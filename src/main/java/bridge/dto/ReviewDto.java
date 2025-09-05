package bridge.dto;

import lombok.Data;

@Data
public class ReviewDto {
	private String content;
	private String userId;
	private String userReviewIdx;
}
