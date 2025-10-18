package bridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bridge.entity.MessageEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer>{
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
	
}
