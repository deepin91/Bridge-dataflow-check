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
	private int roomIdx;
	
	@Column(nullable = false)
	private String userId1;
	
	@Column(nullable = false)
	private String userId2;
}