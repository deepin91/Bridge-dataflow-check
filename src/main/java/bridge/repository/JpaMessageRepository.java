package bridge.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import bridge.entity.MessageEntity;

/* 기본 메시지 조회 기능 */
public interface JpaMessageRepository extends CrudRepository<MessageEntity, Integer> { // CrudRepository 상속

	List<MessageEntity> findByRoomIdx(int roomIdx); // 채팅방에 속한 모든 메시지 조회
	List<MessageEntity> findByRoomIdxOrderByCreatedTimeAsc(int roomIdx); // 시간 순 정렬 포함된 메시지 조회

}
