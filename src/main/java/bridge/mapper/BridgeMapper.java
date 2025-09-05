package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.AnnouncementDto;
import bridge.dto.ApproveResponseDto;
import bridge.dto.CommentsDto;
import bridge.dto.ComposerRequestDto;
import bridge.dto.ComposerRequestTagDto;
import bridge.dto.KakaopayDto;
import bridge.dto.MusicDto;
import bridge.dto.PayListDto;
import bridge.dto.ReportDto;
import bridge.dto.ReviewDto;
import bridge.dto.TagDto;
import bridge.dto.UserDto;
import bridge.dto.UserProfileDto;

@Mapper
public interface BridgeMapper {

	void insertMusic(MusicDto musicDto);

	String selectMusic(String musicUUID);

	// 잼 코맨트
	int insertComments(CommentsDto commentsDto);

	List<CommentsDto> selectCommentsList(int cIdx);

	void updateComments(CommentsDto commentsDto);

	void deleteComments(int ccIdx);

	//신고 작성
	int insertReport(ReportDto reportDto);
	void plusReportCount(String reportedUserId);
	//신고 리스트
	List<ReportDto> openReportList();
	
	//공지 리스트
	List<AnnouncementDto> announcementList();
	//특정 공지
	AnnouncementDto announcementDetail(int aIdx);
	//포인트 충전
	UserDto chargePoint(String userId);

	int doCharge(ApproveResponseDto approveResponse);

	ReportDto openReportDetail(int reportIdx);

	void handleReport(UserDto userDto);

	int selectReportCount(String userId);

	//프로필 작성
	int insertProfile(UserProfileDto userProfileDto);
	//파트너 구인 작성
	int insertPartnerWrite(ComposerRequestDto composerRequestDto);
	//파트너 구인 작성-태그 입력
	void insertPartnerTag(ComposerRequestDto composerRequestDto);

	
	void insertTag(TagDto tag);

	List<UserProfileDto> getPorfile(int idx);

	List<TagDto> getTaglist(int idx);

	List<ReviewDto> getReview(String userId);

	//파트너 구인 태그 작성
	void insertCrtTag(ComposerRequestTagDto crtTag);

	List<ComposerRequestDto> openPartnerList();

	List<ComposerRequestTagDto> partnerTagList();

	ComposerRequestDto openPartnerDetail(int crIdx);

	List<ComposerRequestTagDto> PartnerDetailTag(int crIdx);

	//파트너구인 수정
	void updatePartner(int crIdx);
	//파트너구인 삭제
	void deletePartner(int crIdx);

	void partnerMoney(int pdIdx);

	void moneyToZero();

	int selectIdx(String userId);

	void tagToProfile(TagDto tag);

	int getProfileIdx(String userId);
	

}
