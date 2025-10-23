package bridge.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_message")
public class MessageEntity {
	@Id	//엔티티의 기본키(PK)
//	@GeneratedValue(strategy= GenerationType.AUTO)	// 기본키 생성 전략 (DB에서 제공하는 키 생성 전략을 따른다)
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int messageIdx;
	
	@Column(nullable = false)
	private String writer;
	
	@Column(nullable = false)
//	private int channelId;
	private String data; // 메시지 내용
	
	@Column(nullable = false)
//	@ManyToOne(targetEntity = ChattingEntity.class)
	@JoinColumn(name = "t_chattingroom")
	private int roomIdx;
	
	@Column(nullable = false)
	private LocalDateTime sentAt = LocalDateTime.now();
	
	@Column(nullable = false)
	private LocalDateTime createdTime = LocalDateTime.now(); // 메시지 발송 시간
}	

	/*
	@ManyToOne
    @JoinColumn(name = "room_idx", referencedColumnName = "roomIdx") // 실제 FK 이름과 매핑
    private ChattingEntity chatRoom;
    
    -- 위 같이 수정하면 chatRoom.getRoomIdx() 등으로 엔티티 간 이동 가능
    -- 단, DB에도 t_message.room_idx FK가 있어야 함
	 */

	/*
	 * @JoinColumn만 있고 @ManyToOne이 없어서 실제로는 외래키 연관 관계가 아닌 상태
	 * writer도 단순한 문자열(String)로 저장됨 → UserEntity로 연결은 안 되어 있음
	 */
	
	/* -추가완료- 메세지 주고받을 때 시간 필드 있으면 몇분 전 표시 가능  --  LocalDateTime sentAt 필드 추가 필드 추가 고려 */

	// 채팅은 1:1 구조로 ChattingEntity는 두 명의 유저 조합으로 고유한 채팅방을 구성함
	// MessageEntity는 채팅방과 연결된 메세지 - 작성자, 내용, 시간 등을 포함 roomIdx를 기준으로 채팅방과 연관됨
	// 메세지 송수신은 STOMP 기반 WebSocket으로 처리 > 전송된 메세지는 DB에 저장 -- 추후 채팅방 메세지 목록 API로 조회 가능
