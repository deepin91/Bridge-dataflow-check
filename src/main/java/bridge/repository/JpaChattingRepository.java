package bridge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bridge.entity.ChattingEntity;

/* 채팅방 정보를 처리하는 JPA 인터페이스 */
public interface JpaChattingRepository extends CrudRepository<ChattingEntity, Integer> { // 채팅방 관련 엔티티를 위한 JPA 인터페이스
	
	List<ChattingEntity> findByUserId1(String userId); // 유저1 기준으로 채팅방 목록 조회
	List<ChattingEntity> findByUserId2(String userId); // 유저2 기준으로 채팅방 목록 조회
	
	Optional<ChattingEntity> findByUserId1AndUserId2AndCommissionIdx(String userId1, String userId2, int commissionIdx); // 특정 유저들 간 커미션 기준으로 존재하는 채팅방 찾기
//	@Query("Insert into t_chattingroom(user_id1,user_id2) values(#{userId1},#{userId2})")
//	void insertchatting(@Param("chattingEntity") ChattingEntity chattingEntity);
	
}
