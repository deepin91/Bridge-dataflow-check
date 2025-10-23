package bridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bridge.entity.MessageRead;
import bridge.entity.MessageReadId;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> { // 읽음 상태 관련 Repository 
//	boolean existByMessageIdxAndUserId(int messageIdx, String userId);
//	boolean existsByIdMessageIdxAndUserId(int messageIdx, String userId);
	boolean existsById_MessageIdxAndId_UserId(int messageIdx, String userId); // 메시지를 특정 사용자가 읽었는지 여부 조회
//	List<MessageRead> findByUserId(String userId);
	
	/* 특정 채팅방에서, 특정 유저가 읽지 않은 메시지 수 조회 JPQL */
	/* 해당 채팅방에서 본인이 보낸 메시지는 제외, 본인이 안 읽은 메시지만 필터링 */
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
	    int countUnreadMessages(int roomIdx, String userId); // 쿼리와 매핑되는 메서드 정의

	
    int countById_UserId(String userId); // 전체 안 읽은 메시지 수
    int countById_UserIdAndId_MessageIdx(String userId, int messageIdx);// 특정 메시지 읽음 여부 확인 (선택적)
	
}

// existsByIdMessageIdxAndIdUserId
//→ @EmbeddedId 사용 시 필드 접근 경로는 id.필드명
// countUnreadMessages()
//→ 특정 유저가 안 읽은 메시지 개수 계산용


//--- 이건 Spring Data JPA의 네이밍 쿼리 기능을 사용한 예시
//복합키(@EmbeddedId)를 사용 중이므로 id_필드명 형식으로 접근