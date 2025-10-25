package bridge.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisChatServiceImplTest {

	@Autowired
	private RedisChatService redisChatService;
	
	private final String userId = "testUser";
	private final int messageIdx = 9999;
	
	@BeforeEach
	public void clear() {
		redisChatService.clearReadInfo(messageIdx); // test 전 초기화
	}
	
	@Test
	public void testMarkMessageAsReadAndCheck() {
		redisChatService.markMessageAsRead(messageIdx, userId);
		
		boolean isRead = redisChatService.hasUserReadMessage(messageIdx, userId);
		
		assertThat(isRead).isTrue();
	}
	
	@Test
	public void tesCountUnreadMessages() {
		int roomIdx = 1;
		
		Set<Integer> messageIdsInRoom = new HashSet<>();
		messageIdsInRoom.add(1001);
		messageIdsInRoom.add(1002);
		messageIdsInRoom.add(1003);
		
		Set<Integer> sentByMe = new HashSet<>();
		sentByMe.add(1001); // 내가 보낸 메세지 하나
		
		// mark 하나만 읽음 처리
		redisChatService.markMessageAsRead(1002, userId);
		
		int unreadCount = redisChatService.countUnreadMessages(roomIdx, userId, messageIdsInRoom, sentByMe);
		
		// 읽지 않은: 1003 (1001은 내가 보낸거고, 1002는 이미 읽음처리)
		assertThat(unreadCount).isEqualTo(1);
	}
}
