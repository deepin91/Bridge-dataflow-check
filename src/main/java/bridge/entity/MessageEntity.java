package bridge.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_message")
public class MessageEntity {
	@Id	//엔티티의 기본키(PK)
	@GeneratedValue(strategy= GenerationType.AUTO)	// 기본키 생성 전략 (DB에서 제공하는 키 생성 전략을 따른다)
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
