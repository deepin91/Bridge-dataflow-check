package bridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bridge.dto.AnnouncementDto;
import bridge.dto.CommentsDto;
import bridge.dto.ComposerRequestDto;
import bridge.dto.ComposerRequestTagDto;
import bridge.dto.MusicDto;

import bridge.dto.PayListDto;
import bridge.dto.ReportDto;
import bridge.dto.ReviewDto;
import bridge.dto.TagDto;
import bridge.dto.UserDto;
import bridge.dto.UserProfileDto;
import bridge.mapper.BridgeMapper;
import bridge.mapper.LoginMapper;

@Service
public class BridgeServiceImpl implements BridgeService {

	@Autowired
	BridgeMapper bridgeMapper;
	@Autowired
	LoginMapper loginMapper;
	@Override
	public void insertMusic(MusicDto musicDto) {
		bridgeMapper.insertMusic(musicDto);
	}

	@Override
	public int insertComments(CommentsDto commentsDto) {
		return bridgeMapper.insertComments(commentsDto);

	}

	@Override
	public List<CommentsDto> selectCommentsList(int cIdx) {
		return bridgeMapper.selectCommentsList(cIdx);
	}

	@Override
	public void updateComments(CommentsDto commentsDto) {
		bridgeMapper.updateComments(commentsDto);

	}

	@Override
	public void deleteComments(int ccIdx) {
		bridgeMapper.deleteComments(ccIdx);

	}

	@Override
	public String selectMusic(String musicUUID) throws Exception {
		return bridgeMapper.selectMusic(musicUUID);
	}

	// 신고 작성 시 호출
	@Override
	public int insertReport(ReportDto reportDto) {
		return bridgeMapper.insertReport(reportDto);
	}

	@Override
	public List<AnnouncementDto> announcementList() {
		return bridgeMapper.announcementList();
	}

	@Override
	public AnnouncementDto announcementDetail(int aIdx) {
		return bridgeMapper.announcementDetail(aIdx);
	}

	@Override
	public UserDto chargePoint(String userId) {
		return bridgeMapper.chargePoint(userId);
	}

	@Override
	public List<ReportDto> openReportList() {
		return bridgeMapper.openReportList();
	}

	@Override
	public ReportDto openReportDetail(int reportIdx) {
		return bridgeMapper.openReportDetail(reportIdx);
	}

	@Override
	public void handleReport(UserDto userDto) {
		bridgeMapper.handleReport(userDto);
	}
	
	//프로필 작성
	@Override
	public int insertProfile(UserProfileDto userProfileDto) {
		return bridgeMapper.insertProfile(userProfileDto);
	}
	
	//파트너 구인 작성
	@Override
	public int insertPartnerWrite(ComposerRequestDto composerRequestDto, MultipartFile[] files) {	
		return bridgeMapper.insertPartnerWrite(composerRequestDto);
	}

	@Override
	public void insertTag(TagDto tag) {
		// TODO Auto-generated method stub
		bridgeMapper.insertTag(tag);
	}

	@Override
	public List<UserProfileDto> getPorfile(int idx) {
		// TODO Auto-generated method stub
		return bridgeMapper.getPorfile(idx);
	}

	@Override
	public List<TagDto> getTaglist(int idx) {
		// TODO Auto-generated method stub
		return bridgeMapper.getTaglist(idx);
	}

	@Override
	public List<ReviewDto> getReview(String userId) {
		// TODO Auto-generated method stub
		return bridgeMapper.getReview(userId);
	}


	//파트너 구인 태그 작성
	@Override
	public void insertCrtTag(ComposerRequestTagDto crtTag) {
		bridgeMapper.insertCrtTag(crtTag);
		
	}

	@Override
	public List<ComposerRequestDto> openPartnerList() {
		return bridgeMapper.openPartnerList();
	}


	@Override
	public List<ComposerRequestTagDto> partnerTagList() {
		return bridgeMapper.partnerTagList();
	}

	@Override
	public UserDto getUserDto(String userId) {
		return loginMapper.selectUserByUserId(userId);

	}

	@Override
	public void partnerMoney(int pdIdx) {
		bridgeMapper.partnerMoney(pdIdx);
//		bridgeMapper.moneyToZero();
	}

}
