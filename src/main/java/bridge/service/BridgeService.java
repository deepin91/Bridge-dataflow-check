package bridge.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import bridge.dto.AnnouncementDto;
import bridge.dto.CommentsDto;
import bridge.dto.ComposerRequestDto;
import bridge.dto.ComposerRequestTagDto;
import bridge.dto.MusicDto;
import bridge.dto.ReportDto;
import bridge.dto.ReviewDto;
import bridge.dto.TagDto;
import bridge.dto.UserDto;
import bridge.dto.UserProfileDto;

public interface BridgeService {

	public void insertMusic(MusicDto musicDto);

	public String selectMusic(String musicUUID) throws Exception;

	public int insertComments(CommentsDto commentsDto);

	public List<CommentsDto> selectCommentsList(int cIdx);

	public void updateComments(CommentsDto commentsDto);

	public void deleteComments(int ccIdx);

	// 신고
	public int insertReport(ReportDto reportDto);

	public List<ReportDto> openReportList();

	// 공지
	public List<AnnouncementDto> announcementList();

	public AnnouncementDto announcementDetail(int aIdx);

	// 포인트 충전
	public UserDto chargePoint(String userId);

	public ReportDto openReportDetail(int reportIdx);

	public void handleReport(UserDto userDto);
	
	// 파트너 구인 작성
	public int insertPartnerWrite(ComposerRequestDto composerRequestDto, MultipartFile[] files);

	public int insertProfile(UserProfileDto userProfileDto);


	public void insertTag(TagDto tag);

	public List<UserProfileDto> getPorfile(int idx);

	public List<TagDto> getTaglist(int idx);

	public List<ReviewDto> getReview(String userId);

	//파트너 작업 태그 작성
	public void insertCrtTag(ComposerRequestTagDto crtTag);

	public List<ComposerRequestDto> openPartnerList();

	public List<ComposerRequestTagDto> partnerTagList();
	public UserDto getUserDto(String userId);

	public void partnerMoney(int pdIdx);


}
