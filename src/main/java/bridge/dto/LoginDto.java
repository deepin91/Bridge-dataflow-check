package bridge.dto;

import lombok.Data;

@Data
public class LoginDto {
	private String userId;
	private String userPassword;
	
	private boolean halted;
}
