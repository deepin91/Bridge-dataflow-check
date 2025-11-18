package bridge.dto;

import java.util.List;

import lombok.Data;

@Data
public class ComposerRequestDto {
	
	
	 	private String userId;
//	 	private String userNickname;
	 	private int crtIdx;
	 	private List<String> crtTag;
	 	//
	 	private int crIdx;
		private String crTitle;
		private String crContents;
		private String crPhoto;
		private int crMoney;
		private String crStartDate;
		private String crEndDate;
		private String createdDt;
		private int deletedYn;
		
//	 	private int pdIdx;
//	 	private String userId1;
//	 	private String userId2;
//	 	private int pdMoney;
//	 	private String pdcComment;
//	 	private int pdcIdx;
//	 	private int plMoney;
//	 	private String plDate;
	 	
	
}
