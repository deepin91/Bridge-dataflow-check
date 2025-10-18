package bridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bridge.entity.MessageRead;
import bridge.entity.MessageReadId;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
//	boolean existByMessageIdxAndUserId(int messageIdx, String userId);
//	boolean existsByIdMessageIdxAndUserId(int messageIdx, String userId);
	boolean existsByIdMessageIdxAndIdUserId(int messageIdx, String userId);
//	List<MessageRead> findByUserId(String userId);
	
	@Query("""
	        SELECT COUNT(m)
	        FROM MessageEntity m
	        WHERE m.roomIdx = :roomIdx
	        AND m.writer != :userId
	        AND NOT EXISTS (
	            SELECT r FROM MessageRead r
	            WHERE r.id.messageIdx = m.messageIdx
	              AND r.id.userId = :userId
	        )
	    """)
	    int countUnreadMessages(int roomIdx, String userId);

	
	
}

// existsByIdMessageIdxAndIdUserId
//→ @EmbeddedId 사용 시 필드 접근 경로는 id.필드명
// countUnreadMessages()
//→ 특정 유저가 안 읽은 메시지 개수 계산용