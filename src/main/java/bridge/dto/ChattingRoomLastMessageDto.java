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
	private LocalDateTime createdAt; // 새로생긴 채팅방은 메세지가 없으므로 비교군이 없기때문에 대신 이거로 비교해서 채팅목록 정렬
	
}

// 해당 DTO는 DB 테이블과 1:1 매핑되는 게 아닌 응답 전용으로 사용되는 객체