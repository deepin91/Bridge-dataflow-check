package bridge.dto;

import lombok.Data;

@Data
public class TipCommentsDto {
	private int tbcIdx;
	private String userId;
	private String tbcComments;
	private int tbIdx;
}
