package bridge.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/* 프론트에 전달되는 정보 - 채팅방과 관련된 요약 정보를 하나의 객체로 담아 전달 */
public class ChattingRoomLastMessageDto { 
	private int roomIdx;
	private String userId1;
	private String userId2;
	
	private String lastMessage;
	private LocalDateTime lastSentTime; // 마지막 메시지 시간 (없으면 null)
	private boolean active;
	private LocalDateTime createdAt; // 새로생긴 채팅방은 메세지가 없으므로 비교군이 없기때문에 대신 이거로 비교해서 채팅목록 정렬 기준으로 사용
	
}

// 해당 DTO는 DB 테이블과 1:1 매핑되는 게 아닌 응답 전용으로 사용되는 객체

/// example
// 로그인한 사용자: "userA"
//
//DB 조회
//├─ t_chattingroom (userA가 포함된 방 3개 조회)
//│  ├─ 방1: userA - userB (roomIdx=1)
//│  ├─ 방2: userA - userC (roomIdx=2)
//│  └─ 방3: userD - userA (roomIdx=3)
//
//각 채팅방
//├─ 메시지 조회 (t_chatmessage)
//│  ├─ roomIdx=1 → 메시지 2개 → 마지막 메시지 10:00
//│  ├─ roomIdx=2 → 메시지 없음 → createdAt: 10:30
//│  └─ roomIdx=3 → 메시지 1개 → 마지막 메시지 09:50
//
//정렬
//├─ 10:30 (방2) ← 메시지는 없지만 생성 가장 최근
//├─ 10:00 (방1)
//└─ 09:50 (방3)
//
//→ ChattingRoomLastMessageDto 리스트 반환 (정렬된 순서)