package bridge.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class RedisChatServiceImpl implements RedisChatService {

	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public RedisChatServiceImpl(@Qualifier("redisTemplate")RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/* 메세지 읽음으로 처리 (messageIdx 기준) */
	@Override
	public void markMessageAsRead(int messageIdx, String userId) {
		String key = "read:" + messageIdx;
		redisTemplate.opsForSet().add(key, userId);
		// opsForSet()는 Spring Data Redis에서 제공하는 RedisTemplate의 메서드 중 하나
		// ex) redisTemplate.opsForSet().add("read:room:3:user:hello123", 77);
		// ↘ "read:room:3:user:hello123" 이건 Redis 키 채팅방 3번에서 hello123이 읽은 메시지들 이라는 뜻의
		// key
	} // 77 = 읽은 메시지 ID (예: messageIdx) ---- hello123이 roomIdx = 3에서 77번 메시지를 읽었다는 기록을
		// 남김

	/* 특정 메세지를 해당 유저가 읽었는지 확인 */
	@Override
	public boolean hasUserReadMessage(int messageIdx, String userId) {
		String key = "read:" + messageIdx;
		return redisTemplate.opsForSet().isMember(key, userId);
	}

	/* 채팅방 내 안 읽은 메시지 개수 계산 */
	@Override
	public int countUnreadMessages(int roomIdx, String userId, Set<Integer> messageIdsInRoom, Set<Integer> sentByMe) {
		int count = 0;

		for (Integer messageId : messageIdsInRoom) {
			// 내가 보낸 메세지는 제외하고
			if (sentByMe.contains(messageId))
				continue;
			if (!hasUserReadMessage(messageId, userId)) {
				count++;
			}
		}
		return count;
	}

	/* Redis에서 메세지 읽음 기록 전체 삭제 (optional) */
	@Override
	public void clearReadInfo(int messageIdx) {
		String key = "read:" + messageIdx;
		redisTemplate.delete(key);
	}
}

/// opsForSet()으로 가능한 것?
// add(key, value) - Set에 값 추가
// isMember(key, value) - 값이 Set에 존재하는지 확인
// members(key) - Set에 있는 모든 값 가져오기
// remove(key, value) - Set에서 값 제거
// size(key) - Set 크기 구하기
// union, intersect 등 - 여러 Set 간 연산