package bridge.dto;

import lombok.Data;

@Data
public class AnnouncementDto {
	
	private int aIdx;
	private String aTitle;
	private String aContents;
	private String aDate;
	private String deletedYn;
	private String createdDt;

}
