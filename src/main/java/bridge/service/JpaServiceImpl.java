package bridge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bridge.dto.NoticeDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;
import bridge.mapper.NoticeMapper;
import bridge.repository.JpaChattingRepository;
import bridge.repository.JpaMessageRepository;

@Service
public class JpaServiceImpl implements JpaService {
	@Autowired
	private JpaMessageRepository jpaMessageRepository;
	@Autowired
	private JpaChattingRepository jpaChattingRepository;
	@Autowired
	private NoticeMapper noticeMapper;
	
	@Override
	public List<MessageEntity> getMessage(int roomIdx) {
		return (List<MessageEntity>) jpaMessageRepository.findByRoomIdx(roomIdx);
	}
	
	@Override
	public void insertData(MessageEntity messageEtity) {
		jpaMessageRepository.save(messageEtity);
	}

	@Override
	public ChattingEntity getchatting(int roomIdx) {
		Optional<ChattingEntity> optional = jpaChattingRepository.findById(roomIdx);
		ChattingEntity chatting = optional.get();
		return chatting;
	}

	@Override
	public List<ChattingEntity> getChattingRoom(String userId) {
		List<ChattingEntity> a = (List<ChattingEntity>) jpaChattingRepository.findByUserId1(userId);
		List<ChattingEntity> b = (List<ChattingEntity>) jpaChattingRepository.findByUserId2(userId);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaa" + a+"BBBBBBBBBBBBBBBBBBBBBBBBBBB" + b);
		if(a != null) {
			return a;
		}else {
			return b;
		}
	}
	@Override
	public List<NoticeDto> noticeList() throws Exception {
		return noticeMapper.noticeList();
	}

	@Override
	public void insertNotice(NoticeDto noticeDto) throws Exception {
		noticeMapper.insertNotice(noticeDto);
		
	}

	@Override
	public NoticeDto noticeDetail(int noticeIdx) throws Exception {
		return noticeMapper.noticeDetail(noticeIdx);
	}

	@Override
	public NoticeDto selectNoticeDetail(int noticeIdx) throws Exception {
		return noticeMapper.selectNoticeDetail(noticeIdx);
	}

	@Override
	public int updateNotice(NoticeDto noticeDto) throws Exception {
		return noticeMapper.updateNotice(noticeDto);
	}

	@Override
	public int deleteNotice(int noticeIdx) throws Exception {
		return noticeMapper.deleteNotice(noticeIdx);
	}

	@Override
	public int selectNoticeListCount() throws Exception {
		return noticeMapper.selectNoticeListCount();
	}
	@Override
    public void openChat(ChattingEntity chattingEntity) {
		if(jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId1(),chattingEntity.getUserId2()).size() < 1
		        && jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId2(),chattingEntity.getUserId1()).size() < 1
				) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	        jpaChattingRepository.save(chattingEntity);

		}
		System.out.println(">>>>>>>>>>>오픈챗 서비스");
        jpaChattingRepository.save(chattingEntity);
        System.out.println(">>>>>>>>>>>오픈챗 서비스 나옴");
    }


}
