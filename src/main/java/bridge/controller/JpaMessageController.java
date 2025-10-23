package bridge.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.ChattingRoomLastMessageDto;
import bridge.dto.UserDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;
import bridge.service.JpaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JpaMessageController {

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@Autowired
	private JpaService jpaService;

	/* 로그인된 유저 기준으로 채팅방 목록을 조회하는 API */
	@Operation(summary = "채팅 목록 조회")
	@GetMapping("/api/chatroom")
	public ResponseEntity<Map<String, Object>> chatroom(Authentication authentication) { 
		// 1. 인증 실패 상황 처리
		if (authentication == null) { // 인증 객체가 없으면 (=비로그인 상태) 아래의 에러메세지 반환
			Map<String, Object> error = new HashMap<>();
			error.put("message", "인증되지 않은 사용자입니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}

		// 2. 정상 처리
		UserDto userDto = (UserDto) authentication.getPrincipal(); // 인증 객체에서 사용자 정보 추출

		// DTO(마지막 메시지 포함된)로 채팅방 목록 가져오기
		List<ChattingRoomLastMessageDto> chattingList = jpaService.getChattingRoomMessage(userDto.getUserId()); // 사용자의 채팅방 목록과 마지막 메시지를 DTO 형태로 조회

		Map<String, Object> map = new HashMap<>();
		map.put("sender", userDto.getUserId()); // 프론트에 반환할 데이터 구성 (내 ID, 채팅방 리스트 포함)
		map.put("chatting", chattingList); // 이제 lastMessage 필드가 있는 DTO가 들어감
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	/* 새로운 채팅방을 만들거나 기존 방을 입장하는 API */
	@Operation(summary = "채팅방 열기 or 기존 방 입장") // --9/18 openChat() 메서드 삭제 후 해당 코드 추가
	@PostMapping("/api/chatroom")
	public ResponseEntity<Map<String, Object>> openOrEnterChatRoom(@RequestBody ChattingEntity chattingEntity) {
		int roomIdx = jpaService.openOrFindChat(chattingEntity); // 이미 존재하는 채팅방을 찾거나 새로 생성

		Map<String, Object> response = new HashMap<>(); // 응답에 채팅방 ID와 메시지 포함
		response.put("roomIdx", roomIdx);
		response.put("message", "채팅방 입장 완료");

		return ResponseEntity.status(HttpStatus.OK).body(response); // 응답 반환
	}

	/* 채팅방 입장 시 이전 대화 불러오기(조회) API */
	@Operation(summary = "채팅 작성")
	@GetMapping("/api/chat/{roomIdx}") // 채팅방 ID 기반 GET 요청
	public ResponseEntity<Map<String, Object>> connect(@PathVariable("roomIdx") int roomIdx,
			Authentication authentication) {
		if (authentication == null) { // 인증 여부 확인 (로그인 여부겠지)
			Map<String, Object> error = new HashMap<>();
			error.put("message", "인증이 필요합니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}

		Map<String, Object> map = new HashMap<>();

		List<MessageEntity> messageList = jpaService.getMessageOrdered(roomIdx); // 해당 채팅방 메시지 목록 조회
		map.put("messagelist", messageList); // 응답에 포함

		ChattingEntity chatInfo = jpaService.getchatting(roomIdx); // 해당 채팅방 정보도 함께 가져와서
		map.put("chatting", chatInfo); // 응답에 포함

		UserDto userDto = (UserDto) authentication.getPrincipal(); // 로그인된 유저 정보 가져오기
		// ↓ 이 유저가 글 작성자인지 확인
		boolean isClient = userDto.getUserId().equals(chatInfo.getCommissionWriterId()); // userDto에서 조회한 userId가 chatInfo=채팅방 정보에서 가져온 커미션 글 작성자의 아이디와 같으면 true, 다르면 false
		// 즉, 현재 사용자 ID가 커미션 작성자 ID와 같으면 이 사람을 clinet라고 봄
		map.put("isClient", isClient); // 프론트에 넘김

		return ResponseEntity.status(HttpStatus.OK).body(map); // 성공 응답
	}

	/* WebSocket 메시지 전송 핸들러 */
	@Operation(summary = "채팅 메시지 전송 (WebSocket) - STOMP /pub/chat/message") // WebSocket 메세지는 Swagger에 뜨지 않음- 설명용으로 자세히
	@MessageMapping("/chat/message") // STOMP 메시지 전송 경로 설정
	public void message(MessageEntity message) { // 메시지 수신 메서드~
		// 메시지 전처리: 시간 설정
		message.setCreatedTime(LocalDateTime.now()); // 수신한 메시지에 현재 시각 설정
		message.setSentAt(LocalDateTime.now());
//		// DB 저장
//		jpaService.insertData(message);
		
		// DB에 메시지 저장 후 저장된 메시지 반환 받음
		MessageEntity savedMessage = jpaService.insertData(message); // 저장 후 반환
		System.out.println("📩 저장된 메시지: idx=" + savedMessage.getMessageIdx() + ", writer=" + savedMessage.getWriter());
		
		// 저장된 메시지를 구독자에게 브로드캐스팅 (writer, messageIdx 포함)
		simpMessageSendingOperations.convertAndSend("/sub/channel/" + savedMessage .getRoomIdx(), savedMessage );
		// 메시지 브로드캐스트 (구독자들에게 전달)
//		simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getRoomIdx(), message);
	}

	/* 채팅방 + 마지막 메시지 목록 조회 */
	@Operation(summary = "채팅방 목록 (마지막 메시지 포함)")
	@GetMapping("/api/chatroom/list") // 채팅방과 마지막 메시지 리스트 제공 API
	public ResponseEntity<List<ChattingRoomLastMessageDto>> chatroomWithLastMessage(Authentication authentication) {
		if (authentication == null) { // 인증 여부 확인
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		UserDto userDto = (UserDto) authentication.getPrincipal(); // 사용자 정보 추출
		List<ChattingRoomLastMessageDto> result = jpaService.getChattingRoomMessage(userDto.getUserId()); // 사용자 채팅방 목록 + 마지막 메시지 조회 서비스 호출
		// userDto.getUserId()를 인자로 넘겨서 jpaService.getChattingRoomMessage(String userId) 메서드를 호출 > 그 반환값(제네릭 타입이 List<ChattingRoomLastMessageDto>)을 변수 result에 담음
		return ResponseEntity.ok(result); // 응답으로 반환
	}

	/* ------------이건 필요한지 아닌지 잘 모르겠음 정리 끝난 후 삭제 해도 되면 지울 것------------ */
	// ⚠️ 레거시: 역할 변경 API — 사용 금지 권고 (commission 작성자는 바뀌면 안 됨) 
	@Operation(summary = "(레거시) 채팅방 역할 갱신 - commissionWriterId 수정 (사용 비권장)")
	@PutMapping("/api/chat/{roomIdx}/updateRole")
	public ResponseEntity<?> updateCommissionWriter(@PathVariable int roomIdx, Authentication authentication) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body("commissionWriterId는 커미션 글 작성자로 고정되어야 합니다. 이 API 사용을 중지하세요.");
	}

	/* 채팅방 종료 */
	@Operation(summary = "채팅방 비활성화 (작업 완료)")
	@PutMapping("/api/chat/{roomIdx}/close")
	public ResponseEntity<String> closeChatRoom(@PathVariable("roomIdx") int roomIdx) {
		jpaService.closeChatRoom(roomIdx);// 채팅방 비활성 처리 (DB에서 active = false 설정)
		return ResponseEntity.ok("채팅방이 종료되었습니다."); // 성공 메시지 반환
	}

	/* 채팅 읽음/안읽음 - 단일 메시지 읽음 처리 */
	@Operation(summary = "채팅 읽음 처리 (개별)")
	@PutMapping("/api/chat/read/{messageIdx}")
	public ResponseEntity<?> markAsRead(@PathVariable("messageIdx") int messageIdx, Authentication authentication){
		if(authentication == null) { // 인증 여부 확인
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
		}
		UserDto user = (UserDto) authentication.getPrincipal(); // 로그인 유저 정보 추출
		jpaService.markMessagesAsRead(messageIdx, user.getUserId()); // 서비스 계층 호출: 메시지 읽음 상태로 기록
		
		return ResponseEntity.ok("단일 메세지 읽음 처리 완료");
	}
	
	/* 특정 채팅방의 안 읽은 메시지 수 조회 */
	@Operation(summary ="읽지 않은 메세지 수 카운트(조회)")
	@GetMapping("/api/chat/{roomIdx}/unread") //요청 매핑
	public ResponseEntity<Integer> getUnreadCount(@PathVariable("roomIdx") int roomIdx, Authentication authentication){
		if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		UserDto user = (UserDto) authentication.getPrincipal(); // 로그인 사용자 ID 추출
		int count = jpaService.countUnreadMessages(roomIdx, user.getUserId()); // 서비스 호출: 읽지 않은 메시지 수 계산
		return ResponseEntity.ok(count);
	}
	
	/* 채팅방의 메시지를 일괄 읽음 처리 */
	@Operation(summary = "채팅 읽음 처리 (일괄)")
	@PutMapping("/api/chat/{roomIdx}/read")
	public ResponseEntity<?> markMessagesReadAll(@PathVariable("roomIdx") int roomIdx, 
			@RequestBody Map<String, Integer> body,  
			Authentication authentication) { // PUT 매핑 + 바디 수신
		
		if(authentication == null) { //인증 확인
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}
		String userId = ((UserDto) authentication.getPrincipal()).getUserId(); // 사용자 ID 추출
		Integer lastReadMessageIdx = body.get("lastReadMessageIdx"); // 마지막으로 읽은 메시지 인덱스 파싱(원하는 특정정보만 뽑아옴)
		
		if(lastReadMessageIdx == null) { // 마지막으로 읽은 메세지가 null인지 check
			return ResponseEntity.badRequest().body("lastReadMessageIdx 누락됨");
		}
		//어떤 roomIdx (채팅방)에서 userId가 lastReadMessageIdx까지의 메시지들을 읽음 처리 - 서비스호출
		jpaService.markMessagesAsReadUpTo(roomIdx, userId, lastReadMessageIdx); // 서비스 호출: 해당 메시지까지 읽음 처리
		return ResponseEntity.ok("채팅방 메세지 읽음 일괄처리 완료");	 // 반환
	}
	
	/* 안 읽은 메시지 수 조회 */
	@Operation
	@GetMapping("/api/chat/unread/{roomIdx}") // 요청 매핑
	public int countUnreadMessages(@PathVariable("roomIdx") int roomIdx, Authentication authentication) {
		String userId = ((UserDto) authentication.getPrincipal()).getUserId(); // 로그인 유저 ID 추출
		return jpaService.countUnreadMessages(roomIdx, userId); // 서비스 호출 후 메시지 수 반환
	}
	
	
	/* 전체 안 읽은 메시지 수 조회 -- 상단 네비바에 채팅 알림 표시하기 위함(안읽은 모든 메세지 수 총합 노출) */
	@Operation(summary = "전체 읽지 않은 메시지 개수 반환")
	@GetMapping("/api/chat/unread-count")
	public ResponseEntity<Integer> getTotalUnreadCount(Authentication authentication) {
	    if (authentication == null) { // 로그인 상태 확인(인증 객체 검사)
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    String userId = ((UserDto) authentication.getPrincipal()).getUserId(); // 로그인 사용자 ID 추출
	    int totalUnread = jpaService.countUnreadMessagesAll(userId); // 이 userId를 인자로 갖는 countUnreadMessagesAll 서비스 호출해서 그 값을 int 타입의  totalUnread에 담음
	    return ResponseEntity.ok(totalUnread); // 위의 결과값(안읽은 메시지 수)을 담아 응답으로 반환
	}
}	
//	@Operation(summary="채팅방 역할 갱신 - commissionWriterId 수정")
//	@PutMapping("/api/chat/{roomIdx}/updateRole")
//	public ResponseEntity<?> updateCommissionWriter(
//			@PathVariable int roomIdx,
//			Authentication authentication) {
//
//		// 1. 인증 체크
//		if (authentication == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//		}
//
//		// 2. 현재 로그인한 사용자 ID 가져오기
//		String userId = ((UserDto) authentication.getPrincipal()).getUserId();
//
//		// 3. 서비스 호출 → DB에서 commissionWriterId 수정
//		jpaService.updateCommissionWriter(roomIdx, userId);
//
//		return ResponseEntity.ok().build();
//	}

/*
 * REST API 경로 통일 -- @GetMapping("/chatroom") → @GetMapping("/api/chatroom") 등
 * RESTful하게 URL 정리
 */