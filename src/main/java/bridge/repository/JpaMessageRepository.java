package bridge.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import bridge.entity.MessageEntity;

public interface JpaMessageRepository extends CrudRepository<MessageEntity, Integer> {

	List<MessageEntity> findByRoomIdx(int roomIdx);
	List<MessageEntity> findByRoomIdxOrderByCreatedTimeAsc(int roomIdx);

}
