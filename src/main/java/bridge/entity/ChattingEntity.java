package bridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Entity
@Table(name = "t_chattingroom")

public class ChattingEntity {
	@Id	//엔티티의 기본키(PK)
	@GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL과 H2(테스트) 호환되도록 변경 -- 09/15
//	@GeneratedValue(strategy= GenerationType.AUTO)	// 기본키 생성 전략 (DB에서 제공하는 키 생성 전략을 따른다)
	@Column(name = "room_idx")
	private int roomIdx;
	
	@Column(nullable = false)
	private String userId1;	// 보통 커미션 글 작성자(의뢰인) - client
	
	@Column(nullable = false)
	private String userId2; // 제작자 - producer
	
	@Column(nullable = false)
	private int commissionIdx; // 어떤 커미션글에서 생성된 채팅방인지 구분

	@Column(nullable = false)
	private String commissionWriterId; // 커미션글 작성자 ID 명시 (역할 구분용) - 프론트에서 isClient/isProducer로 판단

	@Column(nullable = false)
	private boolean active = true; // 채팅방 유효 여부 (삭제, 종료 처리용) - 협업 완료 시 방 닫을 때 사용 가능
}