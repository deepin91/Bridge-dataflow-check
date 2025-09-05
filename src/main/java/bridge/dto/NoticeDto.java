package bridge.dto;

import lombok.Data;

@Data
public class NoticeDto {
		private int noticeIdx;
//		private String userId;
		private String writer;
		private String title;
		private String contents;
		private String updatedDt;
		private String createdDt;
}
