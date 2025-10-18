package bridge.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "message_reads")
public class MessageRead  {
	
	@EmbeddedId
    private MessageReadId id;
	
	@Column(name = "read_at", nullable = false)
	private LocalDateTime readAt = LocalDateTime.now();
}
