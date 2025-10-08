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
    
    @Operation(summary="채팅 목록 조회")
    @GetMapping("/api/chatroom")
    public ResponseEntity<Map<String,Object>> chatroom(Authentication authentication){
    	// 1. 인증 실패 상황 처리
        if (authentication == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    	
        // 2. 정상 처리
    	UserDto userDto = (UserDto) authentication.getPrincipal();
    	
    	// DTO(마지막 메시지 포함된)로 채팅방 목록 가져오기
    	List<ChattingRoomLastMessageDto> chattingList = jpaService.getChattingRoomMessage(userDto.getUserId());
    	
    	Map<String,Object> map = new HashMap<>();
    	map.put("sender", userDto.getUserId());
    	map.put("chatting", chattingList); // 이제 lastMessage 필드가 있는 DTO가 들어감
    	return ResponseEntity.status(HttpStatus.OK).body(map);
    }
    
//    @Operation(summary="채팅방 열기")
//    @PostMapping("/api/chatroom")
//    public void openChat(@RequestBody ChattingEntity chattingEntity){
//    	System.out.println(">>>>>>>>>>>>>>>>>>>> 오픈챗 실행");
//        jpaService.openChat(chattingEntity);
//
//        System.out.println(">>>>>>>>>>>>>>>> 오픈챗 종료");
//    }
    
    @Operation(summary="채팅방 열기 or 기존 방 입장") // --9/18 openChat() 메서드 삭제 후 해당 코드 추가
    @PostMapping("/api/chatroom")
    public ResponseEntity<Map<String, Object>> openOrEnterChatRoom(@RequestBody ChattingEntity chattingEntity) {
        int roomIdx = jpaService.openOrFindChat(chattingEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("roomIdx", roomIdx);
        response.put("message", "채팅방 입장 완료");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /* 채팅방 입장 시 이전 대화 불러오기 API */
    @Operation(summary="채팅 작성")
    @GetMapping("/api/chat/{roomIdx}")
	public ResponseEntity<Map<String, Object>> connect(@PathVariable("roomIdx") int roomIdx){
    	Map<String,Object> map = new HashMap<>();
    	List<MessageEntity> MessageEntity = jpaService.getMessage(roomIdx);
    	map.put("messagelist", MessageEntity);
    	ChattingEntity chattingEntity = jpaService.getchatting(roomIdx);
    	
    	map.put("chatting",chattingEntity);
    	return ResponseEntity.status(HttpStatus.OK).body(map);
    }
    
    @Operation(summary="채팅 메시지 전송 (WebSocket) - STOMP /pub/chat/message") // WebSocket 메세지는 Swagger에 뜨지 않음- 설명용으로 자세히   
    @MessageMapping("/chat/message") // 경로 구체적으로 다시 네이밍 ex) /chat/message
    public void message(MessageEntity message) {
    	// 메시지 전처리: 시간 설정
        message.setCreatedTime(LocalDateTime.now());
        
        // DB 저장
        jpaService.insertData(message);
        
        // 메시지 브로드캐스트 (구독자들에게 전달)
    	simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getRoomIdx(), message);
    }
    
    @Operation(summary="채팅방 목록 (마지막 메시지 포함)")
    @GetMapping("/api/chatroom/list")
    public ResponseEntity<List<ChattingRoomLastMessageDto>> chatroomWithLastMessage(Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        List<ChattingRoomLastMessageDto> result = jpaService.getChattingRoomMessage(userDto.getUserId());
        return ResponseEntity.ok(result);
    }
}

	/* REST API 경로 통일 -- @GetMapping("/chatroom") → @GetMapping("/api/chatroom") 등 RESTful하게 URL 정리 */