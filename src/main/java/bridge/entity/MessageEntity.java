package bridge.entity;

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
	private String data;
	@Column(nullable = false)
//	@ManyToOne(targetEntity = ChattingEntity.class)
	@JoinColumn(name = "t_chattingroom")
	private int roomIdx;
}
