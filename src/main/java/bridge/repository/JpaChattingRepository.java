package bridge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bridge.entity.ChattingEntity;

public interface JpaChattingRepository extends CrudRepository<ChattingEntity, Integer> {
	
	List<ChattingEntity> findByUserId1(String userId);
	List<ChattingEntity> findByUserId2(String userId);
	
	Optional<ChattingEntity> findByUserId1AndUserId2(String userId1, String userId2);
//	@Query("Insert into t_chattingroom(user_id1,user_id2) values(#{userId1},#{userId2})")
//	void insertchatting(@Param("chattingEntity") ChattingEntity chattingEntity);
	
}
