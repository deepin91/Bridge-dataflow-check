package bridge.dto;

import lombok.Data;

@Data
public class PartnerPageDto {

	private int pdIdx;
	private String pcContent;
	private String pcWriter;
	private String pcDate;
	private String pcFile;
	private int plIdx;
	private String userId1;
	private String userId2;
	private int plMoney;
	private String plDate;
}
