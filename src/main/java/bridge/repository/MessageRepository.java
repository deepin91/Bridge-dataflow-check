package bridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bridge.entity.MessageEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer>{
	//  단일 채팅방에서 안 읽은 메시지 수 세는 쿼리
	@Query("""
	        SELECT COUNT(m)
	        FROM MessageEntity m
	        WHERE m.roomIdx = :roomIdx
	        AND m.writer != :userId
	        AND NOT EXISTS (
	            SELECT r FROM MessageRead r
	            WHERE r.id.messageIdx = m.messageIdx AND r.id.userId = :userId
	        )
	    """)
	    int countUnreadMessages(@Param("roomIdx") int roomIdx, @Param("userId") String userId);
	
	// 전체 채팅방에서 안 읽은 메시지 수 카운트 
	@Query("""
	    SELECT COUNT(m)
	    FROM MessageEntity m
	    WHERE m.writer != :userId
	    AND NOT EXISTS (
	        SELECT r FROM MessageRead r
	        WHERE r.id.messageIdx = m.messageIdx AND r.id.userId = :userId
	    )
	""")
	int countUnreadMessagesForUserAcrossRooms(@Param("userId") String userId);
}
