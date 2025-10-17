package bridge.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChattingRoomLastMessageDto {
	private int roomIdx;
	private String userId1;
	private String userId2;
	
	private String lastMessage;
	private LocalDateTime lastSentTime;
	private boolean active;
}

// 해당 DTO는 DB 테이블과 1:1 매핑되는 게 아닌 응답 전용으로 사용되는 객체