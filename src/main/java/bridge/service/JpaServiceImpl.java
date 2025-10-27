package bridge.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bridge.dto.ChattingRoomLastMessageDto;
import bridge.dto.NoticeDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;
import bridge.mapper.NoticeMapper;
import bridge.repository.JpaChattingRepository;
import bridge.repository.JpaMessageRepository;

@Service
public class JpaServiceImpl implements JpaService {
	// Repository 및 Mapper 의존성 주입
	@Autowired
	private JpaMessageRepository jpaMessageRepository; // 채팅 메시지 저장 및 조회
	@Autowired
	private JpaChattingRepository jpaChattingRepository; // 채팅방 생성 및 조회
//	@Autowired
//	private MessageReadRepository messageReadRepository; // 메시지 읽음 여부 처리
//	@Autowired
//	private MessageRepository messageRepository; // JPQL 기반 복잡 쿼리 수행용
	
	@Autowired
	private RedisChatService redisChatService;
	
	@Autowired
	private NoticeMapper noticeMapper; // 공지사항 관련 MyBatis Mapper

	
	/* 해당 채팅방(roomIdx)의 전체 메시지를 가져옴 */
	@Override
	public List<MessageEntity> getMessage(int roomIdx) {
		return jpaMessageRepository.findByRoomIdx(roomIdx); // 정렬되지 않은 메시지 리스트 반환

//		System.out.println("🔎 roomIdx: " + roomIdx);
//		System.out.println("🔎 메시지 수: " + messages.size());
//		for (MessageEntity msg : messages) {
//			System.out.println(" - " + msg.getWriter() + ": " + msg.getData());
//		}
//		return messages;
	}
	
	/* 해당 채팅방 메시지를 생성시간 기준 오름차순으로 정렬해서 조회 */ // 채팅방 입장 시 메시지를 위에서 아래로 시간 순으로 정렬해서 출력
	@Override
    public List<MessageEntity> getMessageOrdered(int roomIdx) {
        return jpaMessageRepository.findByRoomIdxOrderByCreatedTimeAsc(roomIdx);
    }
	
	/* 메시지를 채팅방에 저장 */
	@Override
	public MessageEntity insertData(MessageEntity messageEntity) {
		int roomIdx = messageEntity.getRoomIdx(); //messageEntity의 getRoomIdx메서드를 호출 > roomIdx에 담고

	    ChattingEntity chatRoom = jpaChattingRepository.findById(roomIdx) // 그 값(roomIdx)으로 채팅방 조회 > 없으면 RuntimeException 예외 던짐
	        .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다.")); // 있으면 ChattingEntity타입의 변수 chatRoom에 담음

	    if (!chatRoom.isActive()) { // 해당 방(chatRoom)이 비활성(작업 완료) 상태면 메시지 전송을 차단하고 예외를 던짐
	        throw new IllegalStateException("❌ 이 채팅방은 작업 완료되어 더 이상 메시지를 보낼 수 없습니다.");
	    }
	    return jpaMessageRepository.save(messageEntity);  // 메시지를 DB에 저장 > 저장된 엔티티(보통 PK 등 채워진 상태) 를 그대로 반환.
	}

	/* 채팅방 단건 조회 -- 이거 실제 사용중인지 확인 필 */
	@Override
	public ChattingEntity getchatting(int roomIdx) {
		return jpaChattingRepository.findById(roomIdx) // 주어진 roomIdx로 채팅방 검색
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
	}

	/* 유저가 속한 모든 채팅방 조회 - userId가 user1이거나 user2인 모든 채팅방 검색 */
	@Override
	public List<ChattingEntity> getChattingRoom(String userId) {
		List<ChattingEntity> a = jpaChattingRepository.findByUserId1(userId);
		List<ChattingEntity> b = jpaChattingRepository.findByUserId2(userId);
//		System.out.println("aaaaaaaaaaaaaaaaaaaaaa" + a+"BBBBBBBBBBBBBBBBBBBBBBBBBBB" + b);
//		if(a != null) {
//			return a;
//		}else {
//			return b;
//		}
		List<ChattingEntity> all = new ArrayList<>();
		all.addAll(a);
		all.addAll(b);

		return all;
	}

	/* 공지사항 관련 API */
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

	/*
	 * 개선사항 -- 기존 채팅방 존재 여부 체크 > insert할지 말지 설정해야함 현재 중복체크가 없어서 동일 유저 둘이서 다시 채팅할 경우
	 * 기존 채팅방 불러오기가 아닌 중복된 채팅방이 늘어남
	 */
//	@Override
//    public void openChat(ChattingEntity chattingEntity) {
//		if(jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId1(),chattingEntity.getUserId2()).size() < 1
//		        && jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId2(),chattingEntity.getUserId1()).size() < 1
//				) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	        jpaChattingRepository.save(chattingEntity);  // 여기서 한번 save되고
//
//		}
//		System.out.println(">>>>>>>>>>>오픈챗 서비스");
//        jpaChattingRepository.save(chattingEntity); // 여기서 조건과 상관 없이 save 됨 -> 조건 만족하면 총 2번의 save가 되는 꼴 = 중복방지X 
//        System.out.println(">>>>>>>>>>>오픈챗 서비스 나옴");
//    }

	/* 채팅방 생성 or 기존 채팅방 재입장 + 채팅방 정렬 및 마지막 메시지 처리 */
	@Override
	public int openOrFindChat(ChattingEntity chattingEntity) { // 채팅방을 찾거나(있으면) 새로 만들고(없으면) 그 roomIdx를 반환
		String userA = chattingEntity.getUserId1(); // 누가 누구든 상관 없이 정렬용
		String userB = chattingEntity.getUserId2();

		// 채팅방 중복 방지 - 알파벳 순 정렬
		if (userA.compareTo(userB) > 0) { // 두 ID를 사전순(알파벳) 으로 정렬 - A–B”와 “B–A”가 동일 키로 취급해 같은 두 사람 사이 방이 중복 생성되는 걸 막음 / compareTo가 0이면(동일 아이디) 바꾸지 않음
			chattingEntity.setUserId1(userB);
			chattingEntity.setUserId2(userA);
		}
		/* commissionIdx 포함 → 같은 두 사용자여도 commissionIdx 다르면 다른 방 */
		// 정렬이 적용된 userId1, userId2와 commissionIdx로 기존 방 존재 여부 조회
		Optional<ChattingEntity> existingChatRoom = 
				jpaChattingRepository.findByUserId1AndUserId2AndCommissionIdx(
                        chattingEntity.getUserId1(),
                        chattingEntity.getUserId2(),
                        chattingEntity.getCommissionIdx()
                );
		// 이미 방이 존재하면 새로 만들지 않고 해당 방의 roomIdx를 반환 후 종료
		if (existingChatRoom.isPresent()) {
			return existingChatRoom.get().getRoomIdx();
		}
		/* 아래 내용 좀 더 공부 필요 */
		/// 없으면 생성 (unique 경합 대비)
		/// 경합(race condition) 대비 로직. 같은 타이밍에 두 요청이 들어오면 DB의 UNIQUE 제약(가정)에 걸려 DataIntegrityViolationException이 날 수 있음.
		/// 그때는 “혹시 다른 트랜잭션이 먼저 만들어놨을 수 있으니” 다시 조회해서 있으면 그 roomIdx를 반환.
		/// 그래도 없으면 원래 예외 dup을 다시 던짐(진짜 무결성 문제) 라고 함
	    try {
	        ChattingEntity newChat = new ChattingEntity();
	        newChat.setUserId1(chattingEntity.getUserId1());
	        newChat.setUserId2(chattingEntity.getUserId2());
	        newChat.setCommissionIdx(chattingEntity.getCommissionIdx());
	        newChat.setCommissionWriterId(chattingEntity.getCommissionWriterId());
	        newChat.setCreatedAt(LocalDateTime.now()); // ✅ 추가함
	        return jpaChattingRepository.save(newChat).getRoomIdx();
	    } catch (org.springframework.dao.DataIntegrityViolationException dup) {
	        // 동시에 두 요청이 들어온 경우 등: 다시 조회해서 반환
	        return jpaChattingRepository
	            .findByUserId1AndUserId2AndCommissionIdx(
	                chattingEntity.getUserId1(),
	                chattingEntity.getUserId2(),
	                chattingEntity.getCommissionIdx()
	            )
	            .map(ChattingEntity::getRoomIdx)
	            .orElseThrow(() -> dup);
	    }
//		 return existingChatRoom.map(ChattingEntity::getRoomIdx)
//	                .orElseGet(() -> jpaChattingRepository.save(chattingEntity).getRoomIdx());
	    }

//	 // 있다면 기존 roomIdx 반환, 없다면 새 채팅방 생성
//	    return existingChatRoom.map(ChattingEntity::getRoomIdx)
//	    		.orElseGet(() -> {
//	        ChattingEntity newChat = new ChattingEntity();
//	        newChat.setUserId1(chattingEntity.getUserId1());
//	        newChat.setUserId2(chattingEntity.getUserId2());
//	        newChat.setCommissionIdx(chattingEntity.getCommissionIdx());
//	        newChat.setCommissionWriterId(chattingEntity.getCommissionWriterId());
//	        return jpaChattingRepository.save(newChat).getRoomIdx();
//		});
//	}

//		List<ChattingEntity> direct = jpaChattingRepository.findByUserId1AndUserId2(userA, userB);
//		List<ChattingEntity> reverse = jpaChattingRepository.findByUserId1AndUserId2(userB, userA);
//		
//		ChattingEntity newChatRoom = jpaChattingRepository.save(chattingEntity);
//		return newChatRoom.getRoomIdx();

	
	@Override
    @Transactional
    public void updateCommissionWriter(int roomIdx, String newWriterId) {
        // 역할 오염 방지 목적: 사용 금지. 필요 시 관리자만 허용하도록 추가 검증.
        ChattingEntity entity = jpaChattingRepository.findById(roomIdx)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        // no-op 또는 감사 로깅만 남기고 종료
        // entity.setCommissionWriterId(newWriterId); // 🚫 사용 금지
    }
	
	/*
	 * 다른 메서드들은 CRUD 중심이라 별다른 처리 로직이 없지만 아래의 메서드는 중복 제거 및 정렬 + 마지막 메세지에 대한 데이터를
	 * 뽑아내야하는 등 여러 과정이 필요하기 때문에 보다 간단 명료하고 효율적인 코드 작성을 위해 (구현을 위해) Stream API와
	 * Comparator 체이닝을 활용함
	 */
	
	// 기능 특성상 정렬, 중복 제거, DTO 변환이 필요한 메서드라 단순 반복문보다 Stream 체이닝 방식이 더 적합
	/* 채팅방 목록 조회 및 정렬 */
	/// 현재 로그인한 유저가 속한 채팅방 리스트를 가져오고, 각각의 채팅방에 대해 마지막 메시지를 함께 DTO에 담아 반환하는 메서드
	@Override
	public List<ChattingRoomLastMessageDto> getChattingRoomMessage(String userId) {

//		채팅방은 userId1, userId2 두 필드 중 하나에 해당 유저가 있을 수 있음
//		두 조건 모두 검색한 후 Stream.concat()으로 합치고, 중복 제거 (distinct())
//		최종적으로 List<ChattingEntity> 형태로 저장
		List<ChattingEntity> chatRooms = Stream.concat(
				/*
				 * List.addAll()로 두 리스트를 붙일 수도 있지만 Stream을 쓰면 중간에 filter, map, distinct 등 유연한 중간
				 * 처리들을 체이닝으로 넣을 수 있음
				 */ // 체이닝 - 메서드를 체인처럼 이어서 사용하는 것
				jpaChattingRepository.findByUserId1(userId).stream(), // userId1로 참여한 방
				jpaChattingRepository.findByUserId2(userId).stream() // userId2로 참여한 방
		).distinct() // 동일한 채팅방이 2번 나올 수 있으니 중복제거
				.collect(Collectors.toList());

		/// 각 채팅방별로 메시지 조회 및 DTO 생성 - 결과를 담을 DTO 리스트 선언
		List<ChattingRoomLastMessageDto> result = new ArrayList<>(); // 모든 채팅방 정보를 담을 result 리스트 생성 (ChattingRoomLastMessageDto 타입)
		/* chatRooms 리스트를 순회하면서 각 요소를 chatRoom으로 꺼냄 - chatRooms 리스트의 모든 채팅방에 대해 반복 */
		///각 채팅방마다 반복 실행 > 현재 채팅방의 고유 번호(roomIdx) 추출
		for (ChattingEntity chatRoom : chatRooms) { // chatRooms 리스트 안에있는 ChattingEntity 객체들을 하나씩 꺼내면서 chatRoom 변수에 담고 그
													// chatRoom객체로 반복문을 수행하는 것
			
			/* 현재 순회 중인 채팅방의 고유 ID (roomIdx) 저장 */
			int roomIdx = chatRoom.getRoomIdx(); // 현재 채팅방 index 추출

			/* 해당 채팅방의 모든 메시지를 생성 시간 기준으로 오름차순 정렬하여 불러옴 - 가장 마지막 메세지 추출 위함 */
            List<MessageEntity> messages = jpaMessageRepository.findByRoomIdxOrderByCreatedTimeAsc(roomIdx);
            
			// 해당 채팅방(roomIdx)의 모든 메시지를 조회
			// (해당 채팅방의 메시지 중 가장 마지막 메시지 (createdTime 기준으로 가장 마지막))
//			List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);

			// 메시지가 하나도 없는 경우를 대비한 기본값 초기화
			String lastMessage = "";
			LocalDateTime lastSentTime = null;

			// 메시지가 있으면, 가장 마지막 메시지를 꺼내 내용과 시간 저장
			if (!messages.isEmpty()) {
				MessageEntity last = messages.get(messages.size() - 1); // 마지막 메시지 - 리스트 인덱스는 0부터 시작하므로
				lastMessage = last.getData(); // 메시지 내용
				lastSentTime = last.getCreatedTime(); // 메시지 생성 시간 (필요 시 MessageEntity에 추가)
			}
			// 결과 반환용 DTO 생성 --- 추출한 정보로 DTO 생성 및 채우기
			ChattingRoomLastMessageDto chattingRoomLastMessageDto = new ChattingRoomLastMessageDto();
			
			// ↓ 채팅방 관련 정보 및 마지막 메시지 정보를 DTO에 세팅
			chattingRoomLastMessageDto.setRoomIdx(roomIdx);
			chattingRoomLastMessageDto.setUserId1(chatRoom.getUserId1());
			chattingRoomLastMessageDto.setUserId2(chatRoom.getUserId2());
			chattingRoomLastMessageDto.setLastMessage(lastMessage);
			chattingRoomLastMessageDto.setLastSentTime(lastSentTime);
			chattingRoomLastMessageDto.setActive(chatRoom.isActive());

			/* ++++ 채팅방에 메시지가 없을 경우 정렬을 위한 기준값 (생성시간) 설정 */
			chattingRoomLastMessageDto.setCreatedAt(chatRoom.getCreatedAt()); 
			// 결과 리스트에 추가
			result.add(chattingRoomLastMessageDto);
		}

		// 정렬 기준 수정: lastSentTime이 없으면 createdAt 기준
	    result.sort(Comparator.comparing(
	            dto -> dto.getLastSentTime() != null ? dto.getLastSentTime() : dto.getCreatedAt(),
	            Comparator.reverseOrder()
	    ));
		return result; // <-- 모든 채팅방에 대해 DTO 리스트 반환
	}
	
	    // 자바는 result가 ChattingRoomLastMessageDto 타입의 리스트인 걸 알고 있으므로 람다식에서 dto라고 하면 자동으로 ChattingRoomLastMessageDto 타입으로 추론해서 적어줌
		// ↓ 아래의 방식대로하면 새로생긴 채팅방이 최하단으로 밀리는 현상이 발생
		// 다른 채팅방에서 보낸 메세지가 있어도 그 후에 채팅방이 새로 생성되면 마지막 메세지 시간과 새로운 채팅방 생성 시간을 비교해서 
		// 최신순 정렬 
		
//				ChattingRoomLastMessageDto::getLastSentTime, // 클래스명::메서드명 or 인스턴스::메서드명 형태 																		// --람다식 간단히 줄여쓴 문법
//				Comparator.nullsLast(Comparator.reverseOrder())
//				)); // Comparator.comparing(dto -> dto.getLastSentTime())
																	// 이걸 간단하게 표현한 것
		// Comparator.comparing(...) - 특정 필드를 기준으로 비교하겠다
		// ChattingRoomLastMessageDto::getLastSentTime - 정렬 기준 필드
		// Comparator.nullsLast(...) - null인 값은 맨 뒤로
		// Comparator.reverseOrder() - 최신순(내림차순) 정렬

		// ChattingRoomLastMessageDto 클래스의 getLastSentTime 메서드 필드를 기준으로 비요하여 null값은 맨 뒤에
		// 두고 최신순으로 정렬

		// getLastSentTime() 값을 기준으로 정렬
		// 만약 lastSentTime이 null이 아니면 시간순으로 비교, null인 경우는 리스트 뒤쪽으로 보냄
		// -> reverseOrder로 최근 시간이 맨 위로 오도록 내림차순 정렬

	/* 채팅방 비활성화 */
	@Override
	@Transactional // 이 메서드는 트랜잭션 내에서 실행되며, 실패 시 롤백됨
	public void closeChatRoom(int roomIdx) {
		jpaChattingRepository.findById(roomIdx).ifPresent(chat -> { //람다식 - 변수선언 하지 않고 바로 정의된 매개변수 사용 가능
	        // 채팅방 인덱스로 Optional<ChattingEntity>를 조회 >  존재할 경우 chat으로 받아서 처리
			chat.setActive(false); // 채팅방을 비활성화 상태로 변경 (active 컬럼 false 설정)
	        jpaChattingRepository.save(chat); // 변경된 상태 저장
	    });
	}
	
	/* 특정 메시지 단일 읽음 처리  =>  읽음 여부 t_message_read 테이블에 저장 */
	@Override
	@Transactional
	public void markMessagesAsRead(int roomIdx, String userId) {
		// 1️⃣ 해당 채팅방의 모든 메시지를 DB에서 조회
	    List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);
	    
	    // 2️⃣ 각 메시지 ID를 Redis에 “읽었다”는 기록으로 추가
	    for (MessageEntity message : messages) {
	        redisChatService.markMessageAsRead(message.getMessageIdx(), userId);
//			 해당 메시지 ID와 유저ID 조합으로 읽음 여부 존재 여부 확인
//			 존재하지 않으면 새로운 MessageRead 객체 생성 및 저장
//			 복합 키로 메시지 ID + 유저 ID 사용
//			if (!messageReadRepository.existsById_MessageIdxAndId_UserId(messageIdx, userId)) {
//				MessageRead read = new MessageRead();
//				read.setId(new MessageReadId(messageIdx, userId));
//				read.setReadAt(LocalDateTime.now());
//				messageReadRepository.save(read);
//		/// Redis에 이 유저가 이 메시지를 읽었다는 기록만 남김
//				redisChatService.markMessageAsRead(messageIdx, userId);
			}
	}
	
	
	/* 특정 채팅방에서 해당 유저 기준으로 읽지 않은 메시지 수 반환 (채팅방 별 안읽음 배지처리 위함) */
	/* Redis를 통해 특정 채팅방에서 유저 기준으로 안 읽은 메세지 수를 계산. */
	/// Redis 방식으로 변경 
	@Override
	public int countUnreadMessages(int roomIdx, String userId) {
		// 1️. 채팅방 내 모든 메시지를 불러오기
	    List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);
	    // 2. 해당 채팅방의 메시지 ID 전체 추출
		Set<Integer> messageIdsInRoom = jpaMessageRepository.findMessageIdsByRoomIdx(roomIdx);
		// 3. 내가 보낸 메시지 ID만 따로 추출 (내가 보낸 건 읽음에서 제외)
		Set<Integer> sentByMe = jpaMessageRepository.findMessageIdsByRoomIdxAndWriter(roomIdx, userId);
		// 4. RedisChatService 통해 안 읽은 메시지 개수 계산
		return redisChatService.countUnreadMessages(roomIdx, userId, messageIdsInRoom, sentByMe);
	}
	
	/* 지정된 메시지 번호까지 해당 유저의 메시지를 모두 읽음 처리 
	 * 프론트에서 현재 마지막 메시지까지 다 읽었다면 이 메서드 호출 */
	@Override
	@Transactional
	public void markMessagesAsReadUpTo(int roomIdx, String userId, int lastReadMessageIdx) {
		// 채팅방 내 모든 메시지를 시간순 정렬해 가져옴
//		List<MessageEntity> messages = jpaMessageRepository.findByRoomIdxOrderByCreatedTimeAsc(roomIdx);
		
		// 1️⃣ 채팅방 내 모든 메시지 가져오기
	    List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);
		
		// 2. 지정된 messageIdx까지 반복하며 읽음 처리
		for(MessageEntity message : messages) {
			if(message.getMessageIdx() <= lastReadMessageIdx) {
				redisChatService.markMessageAsRead(message.getMessageIdx(), userId);
			}
		}
	}
	
	/* 로그인한 유저 기준, 모든 채팅방을 돌면서 총 안 읽은 메시지 개수 합산 */
	/* 모든 채팅방을 통틀어서 해당 유저 기준 읽지 않은 메시지 개수 반환 (상단 네비바에 총 읽지 않은 메세지 표시 위함) */
	@Override
	public int countUnreadMessagesAll(String userId) {
		// 참여 중인 모든 채팅방 조회 (userId1 또는 userId2)
		List<ChattingEntity> allChatRooms = jpaChattingRepository.findByUserId1OrUserId2(userId, userId); // 유저가 참여 중인 모든 채팅방 조회
		
		int totalUnread = 0;
		// 각 채팅방별로 Redis에서 안 읽은 메시지 개수를 더함
		for(ChattingEntity room : allChatRooms) {
			int roomIdx = room.getRoomIdx();
//			List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);
			
			// 해당 채팅방의 모든 메시지 ID 가져오기
//			Set<Integer> messageIdsInRoom = extractMessageIds(messages);
			// MessageEntity 중 내가 쓴 메시지의 messageIdx만 추출
//			Set<Integer> sentByMe = extractMyMessageIds(messages, userId);
			// Redis 기반으로 안 읽은 메시지 개수 계산
//			int unreadCount = redisChatService.countUnreadMessages(roomIdx, userId, messageIdsInRoom, sentByMe);
			
			totalUnread += countUnreadMessages(roomIdx, userId);
		}
		return totalUnread;
	}
	
	/* 전체 메세지에서 messageIdx만 추출 
	 * 전체 메시지 목록에서 messageIdx만 추출해서 Set으로 반환.
	 */
	private Set<Integer> extractMessageIds(List<MessageEntity> messages) {
	    return messages.stream()
	        .map(MessageEntity::getMessageIdx)
	        .collect(Collectors.toSet());
	}

	/* 내가 작성한 메시지의 ID만 추출 */
	private Set<Integer> extractMyMessageIds(List<MessageEntity> messages, String userId) {
	    return messages.stream()
	        .filter(m -> userId.equals(m.getWriter()))
	        .map(MessageEntity::getMessageIdx)
	        .collect(Collectors.toSet());
	}
} 
	// 대화상대, 마지막 메세지, 시간 까지 응답
	// 읽음처리는 추후 설정 고려중
	// *****프론트에서 실시간으로 채팅목록 (최신순으로) 정렬하려면 따로 설정해야함****


/* stream().forEach() - 단순 반복뿐 아니라 필터링, 변환, 정렬 등을 자유롭게 다룰 수 있음 */

// 기존에는 for-each 문을 사용하여 (채팅목록을) 단순 반복처리했지만 채팅방 중복 생성 및 정렬 이슈가 발생하여
// 이를 개선하기 위해 Stream API와 Comparator 체이닝을 도입하여 기존의 코드를 간결하고 명확하게 개선하였습니다.

// 기존의 for-each문 구성에선 채팅방 중복체크 및 정렬이 제대로 관리되지 않는 문제가 있었으나
// 람다식과 Comparator 활용으로 가독성 높은 최신순 정렬 로직을 구현
/*
 * -- 채팅방 중복 제거 및 최신순 정렬을 위해 Stream API와 Comparator 체이닝을 도입(사용)하여 기존 for-each
 * 기반의 코드를 간결하고 명확하게 (처리)개선했습니다
 */
