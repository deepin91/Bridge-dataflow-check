package bridge.dto;

import lombok.Data;

@Data
public class UserProfileDto {

	private int userProfileIdx;
	//인증 지금 없음 => 빼는건지 기능 넣는건지 결정 필요
	private boolean userCertification;
	
	private String userId;
	private String userPosition;
	private String userIntroduction;
	private String profileImg;
	private String bannerImg;
	private String userSite;
	private String userPhoto;

	private String userTag1;
	private String userTag2;
	private String userTag3;
	private String userTag;
	private String userMusic;
	
	private String userPortfolio;

}

