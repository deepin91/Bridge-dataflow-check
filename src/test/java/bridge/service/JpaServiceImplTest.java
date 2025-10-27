package bridge.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;

@SpringBootTest(classes = bridge.BridgeApplication.class)
public class JpaServiceImplTest {
	
	@Autowired
	private JpaService jpaService;
	
	@Test
	public void testGetMessage() {
		int testRoomIdx = 1; // 실제 존재하는 roomIdx 필요
		List<MessageEntity> messages = jpaService.getMessage(testRoomIdx);
		
		assertThat(messages).isNotNull();
	}
	
	@Test
	public void testCountUnreadMessagesAll() {
		String testUserId = "test"; // 실제 존재하는 유저 ID 필요
		int count = jpaService.countUnreadMessagesAll(testUserId);
		
		assertThat(count).isGreaterThanOrEqualTo(0);
	}
	
	@Test
	public void testOpenOrFindChat() {
		ChattingEntity chat = new ChattingEntity();
		chat.setUserId1("userA");
		chat.setUserId2("userB");
		chat.setCommissionIdx(111);
		chat.setCommissionWriterId("userA"); // ✅ 필수 추가
	    chat.setCreatedAt(LocalDateTime.now()); // ✅ 필수 추가
		
		int roomIdx = jpaService.openOrFindChat(chat);
		
		assertThat(roomIdx).isGreaterThan(0);
	}
}
