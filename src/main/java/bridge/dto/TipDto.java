package bridge.dto;

import lombok.Data;

@Data
public class TipDto {
	private int tbIdx;
	private String tbTitle;
	private String tbContents;
	private int tbViews;
	private String userId;
	private int tbHeart;
	private String tbCreatedt;
	private boolean tb_deleted;
	
}
