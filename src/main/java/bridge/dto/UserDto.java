package bridge.dto;

import lombok.Data;

@Data
public class UserDto {
	private String userId;
	private String userPassword;
	private String userName;
	private String userEmail;
	private String userPhoneNumber;
	private int userPoint;
	private boolean userHalted;
	private int reportCount;

	private String role; // "CLIENT" or "PRODUCER"
	private String provider;
}
