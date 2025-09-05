package bridge.service;

import java.util.List;

import bridge.dto.NoticeDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;

public interface JpaService {
	List<MessageEntity> getMessage(int roomIdx);
	void insertData(MessageEntity messageEtity);
	ChattingEntity getchatting(int roomIdx);
	List<ChattingEntity> getChattingRoom(String userId);
	List<NoticeDto> noticeList()throws Exception;
	void insertNotice(NoticeDto noticeDto)throws Exception;
	NoticeDto noticeDetail(int noticeIdx)throws Exception;
	NoticeDto selectNoticeDetail(int noticeIdx)throws Exception;
	int deleteNotice(int noticeIdx)throws Exception;
	int updateNotice(NoticeDto noticeDto)throws Exception;
	int selectNoticeListCount() throws Exception;
	void openChat(ChattingEntity chattingEntity);

}
