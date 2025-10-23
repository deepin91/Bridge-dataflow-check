package bridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bridge.entity.MessageEntity;

/* 메시지 관련 쿼리를 담당하는 JPA Repository 인터페이스 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer>{ // 메시지 관련 DB 조작 인터페이스
	
//	특정 채팅방에서 안 읽은 메시지 수 조회하는 JPQL
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
	

//	전체 채팅방에서 안 읽은 메시지 수 카운트
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


// 사용 목적: 상단 알림 표시, 채팅방 리스트 정렬 판단 근거 등
// NOT EXISTS 쿼리로 읽지 않은 메시지만 카운팅