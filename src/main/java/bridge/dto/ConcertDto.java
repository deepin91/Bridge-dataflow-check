package bridge.dto;

import lombok.Data;

@Data
public class ConcertDto {
	private int cIdx;
	private String cTitle;
	private String cContents;
	private String cWriter;
	private String cPhoto;
	
	private String title;
	private String content;
}
