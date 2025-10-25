package bridge.service;

import java.util.Set;

public interface RedisChatService {
	void markMessageAsRead(int messageIdx, String userId);
    boolean hasUserReadMessage(int messageIdx, String userId);
    int countUnreadMessages(int roomIdx, String userId, Set<Integer> messageIdsInRoom, Set<Integer> sentByMe);
    void clearReadInfo(int messageIdx);
}
