package bridge.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bridge.dto.AnnouncementDto;
import bridge.dto.MusicDto;
import bridge.dto.ReportDto;
import bridge.dto.UserDto;
import bridge.mapper.BridgeMapper;
import bridge.service.BridgeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
public class RestApiController {

	@Autowired
	private BridgeService bridgeService;

	@Autowired
	private BridgeMapper bridgeMapper;

	@Operation(summary = "음악 파일 조회")
	@GetMapping("/api/getMusic/{musicUUID}") 
	// MP3 재생 (원본)
	/* 요청된 musicUUID에 해당하는 UUID.mp3 파일을 서버에서 찾아 브라우저에 스트리밍 재생하도록 응답
	 * response.getOutputStream()에 1024 바이트 단위로 mp3 데이터를 전송
	 * Content-Disposition: inline; → 다운로드가 아닌 브라우저 내 재생
	 */
	public void getMusic(@PathVariable("musicUUID") String musicUUID, HttpServletResponse response) throws Exception {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
//		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
//		"C:/docker/music/"
		String UPLOAD_PATH = "C:/docker/music/";
		
	    // 로그 찍기 (여기서)
	    System.out.println("🎵 전달받은 musicUUID: " + musicUUID);
	    
		System.out.println(">>>>>>>>>>>>>>>>>>>>    " + musicUUID);
		System.out.println("++++++++++++++++++++++" + response);
		try {
			response.setHeader("Content-Disposition", "inline;");
			byte[] buf = new byte[1024];
			fis = new FileInputStream(UPLOAD_PATH + musicUUID + ".mp3");
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			int read;
			while ((read = bis.read(buf, 0, 1024)) != -1) {
				bos.write(buf, 0, read);
			}
		} finally {
			bos.close();
			bis.close();
			fis.close();
		}
	}

	/* Docker 컨테이너 실행(음원 분리 시작) -- /api/docker/{musicUUID}
	 * 위에서 저장한 UUID로 mp3를 찾아 Docker 컨테이너를 실행
	 * 해당 mp3 파일을 입력으로 Spleeter Docker 컨테이너를 실행해서 음원을 stem 단위로 분리.
	 * docker container run -d --rm -w /my-app -v  c:\test:/my-app sihyun2/spleeter  /bin/bash -c "spleeter separate -p spleeter:5stems -o output "a3b1f8d1.mp3
	 * ↑ 실행 명령어
	 * 분리 결과는 서버 내부 output/{UUID}/ 폴더에 저장됨
	 */
	@Operation(summary = "음원 분리 컨테이너 실행")
	@GetMapping("/api/docker/{musicUUID}")
	public ResponseEntity<Map<String, Object>> dockerList(@PathVariable("musicUUID") String musicUUID)
			throws Exception {

		String musicUuid = musicUUID + ".mp3";

//		final String command = "docker container run -d --rm -w /my-app -v  c:\\test:/my-app sihyun2/spleeter  /bin/bash -c \"spleeter separate -p spleeter:5stems -o output \""  // 실행하는 명령어
//				+ musicUuid;
		final String command = "docker container run -d --rm -w /my-app -v  C:/docker/music:my-app deezer/spleeter:3.8-5stems /bin/bash -c \"spleeter separate -p spleeter:5stems -o output \""  // 실행하는 명령어
				+ musicUuid;
		// -- spleeter:5stems는 보컬, 드럼, 피아노, 기타, 기타로 음원을 분리
		// 실행되면 output/{UUID}/ 디렉토리에 분리된 mp3 파일들이 생김
		Process process = null;
		Map<String, Object> result = new HashMap<>();
		List<String> uuids = new ArrayList<>();
		result.put("uuids", uuids);
		try {
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(result);
	}
	
	/* Docker 컨테이너 실행 여부 확인
	 * 현재 ~~~~/spleeter 컨테이너가 실행 중인지 확인
	 */
	@Operation(summary = "컨테이너 실행 여부 조회")
	@GetMapping("/api/IsDockerRun")
	public ResponseEntity<Boolean> isDockerRun() {
		final String command = "docker container ls"; // docker container ls 명령어로 실행 중인 컨테이너 목록을 불러옴
		boolean isRunning = false;
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			List<String> list = reader.lines().toList();

			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.contains("sihyun2/spleeter")) {
					isRunning = true;  // 그 중 spleeter 이미지가 있으면 true 반환
					break;
				}
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + isRunning);
		return ResponseEntity.ok(isRunning);
	}

	/* 분리된 음원 목록 조회
	 * 위에서 생성된 output/UUID/ 폴더에 있는 분리된 mp3 파일 목록을 반환
	 * (output/{UUID}/ 경로에 존재하는 분리된 wav/mp3 파일들의 파일명 리스트 반환)
	 */
	@Operation(summary = "분리된 음원 폴더 조회")
	@GetMapping("/api/splitedMusic/{musicUUID}")
	public List<String> splitedMusic(@PathVariable("musicUUID") String musicUUID) throws Exception {
//		String path = "C:/home/ubuntu/temp/output/" + musicUUID + "/";
		String path = "C:/docker/spleeter/output/" + musicUUID + "/";
		File file = new File(path); // 예시 결과 -["vocals.wav", "drums.wav", "piano.wav", "bass.wav", "other.wav"]

		File[] files = file.listFiles();
		List<String> fileNames = new ArrayList<>();

		for (File f : files) {
			String fileName = f.getName();
			fileNames.add(fileName);
			System.out.println("=================" + fileNames);
		}
		return fileNames;
	};
	
	/* 특정 stem 스트리밍 재생
	 * output/{UUID}/{파일명} 파일을 브라우저에서 스트리밍 재생하게끔 응답
	 */
	@Operation(summary = "분리된 음원 재생")
	@GetMapping("/api/getSplitedMusic/{musicUUID}/{fn}") // 특정 stem 재생
	public void getSplitedMusic(@PathVariable("musicUUID") String musicUUID, HttpServletResponse response,
			@PathVariable("fn") String fn) throws Exception { // fn은 vocals.wav 등 stem 파일명
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
//		String path = "C:/home/ubuntu/temp/output/" + musicUUID + "/" + fn;
		String path = "C:/docker/spleeter/output/" + musicUUID + "/" + fn;
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>    " + musicUUID);
		System.out.println("111111111111111" + fn);
		System.out.println("++++++++++++++++++++++" + response);
		try {
			response.setHeader("Content-Disposition", "inline;");
			byte[] buf = new byte[1024];
			fis = new FileInputStream(path);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			int read;
			while ((read = bis.read(buf, 0, 1024)) != -1) {
				bos.write(buf, 0, read);
			}
		} finally {

		}
	}
	
	
	/* 특정 stem 다운로드 */
	@Operation(summary = "분리된 음원 다운로드")
	@GetMapping("/api/downloadSplitedMusic/{musicUUID}/{fileName:.+}") // 특정 stem 다운로드
	public void downloadSplitedMusic(@PathVariable("musicUUID") String musicUUID,
			@PathVariable("fileName") String fileName, HttpServletResponse response) throws Exception {
//		String filePath = "C:/home/ubuntu/temp/output/" + musicUUID + "/" + fileName;
		String filePath = "C:/docker/spleeter/output/" + musicUUID + "/" + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // Content-Disposition: attachment; → 다운로드 자동 시작
			try (FileInputStream inputStream = new FileInputStream(file);
					ServletOutputStream outputStream = response.getOutputStream()) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	// --음원분리 파트--
	/* 사용자가 mp3 파일을 업로드 -> 서버에 UUID.mp3 형식으로 저장 ex) a3b1f8d1.mp3
	 * + DB에 제목과 UUID 저장
	 */
	@Operation(summary = "분리할 음원 업로드")
	@PostMapping("/api/insertMusicForSplit/{cIdx}")
	public ResponseEntity<Map<String, Object>> insertMusicForSplit(@PathVariable("cIdx") int cIdx,
			@RequestPart(value = "files", required = false) MultipartFile[] files) throws Exception { // 입력 - MultipartFile[] files / cIdx - 연관된 게시글 ID
//		String UPLOAD_PATH = "C:\\home\\ubuntu\\temp\\";
		String UPLOAD_PATH = "C:\\docker\\music\\";
		int insertedCount = 0;
		String uuid = UUID.randomUUID().toString();
		List<String> fileNames = new ArrayList<>();

		Map<String, Object> result = new HashMap<>();

		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename();
				try {
					File f = new File(UPLOAD_PATH + File.separator + uuid + ".mp3"); // 파일명을 UUID.mp3로 저장
					System.out.println("---------------------------" + f);
					mf.transferTo(f); // UUID로 저장

				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				fileNames.add(originFileName);
				insertedCount++;

				MusicDto musicDto = new MusicDto();
				musicDto.setMusicTitle(originFileName);
				musicDto.setMusicUUID(uuid); // // DB에도 저장
				musicDto.setCIdx(cIdx);
				bridgeService.insertMusic(musicDto); // musicDto에 제목/UUID/cIdx 저장 후 bridgeService.insertMusic() 호출
			}

			if (insertedCount > 0) {
				result.put("uuid", uuid);
				result.put("fileNames", fileNames);
				return ResponseEntity.status(HttpStatus.OK).body(result);
			} else {
				result.put("message", "No files uploaded");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "파일 업로드 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	/* 신고하기 */
	@Operation(summary = "신고 작성")
	@PostMapping("/api/report/{reportedUserId}")
	public ResponseEntity<Map<String, Object>> insertReport(@RequestBody ReportDto reportDto, 
			@PathVariable("reportedUserId") String reportedUserId) throws Exception { // musicDto에 제목/UUID/cIdx 저장 후 bridgeService.insertMusic() 호출
			/* 
			 * reportDto - 클라이언트가 보낸 신고 정보를 담은 JSON 객체 ex) { "userId": "신고한 사람", "reason": "욕설" }
			 * reportedUserId - URL 경로에서 추출된 신고당한 사람 ID
			 */
			//응답 타입은 Map<String, Object> 형태의 JSON, ResponseEntity로 상태 코드와 데이터를 같이 보냄
		int insertedCount = 0; // --신고 DB에 실제로 insert 되었는지 확인하기 위한 변수 (성공 시 1, 실패 시 0)
		try {
			reportDto.setReportedUserId(reportedUserId); // 경로에서 받은 reportedUserId를 DTO에 직접 set -JSON으로는 신고자 정보만 들어오고 피신고자는 URL에서 받음
//			reportDto.getUserId(); // 신고자ID 가져옴 ********사용되지않는 코드임 -제거
			insertedCount = bridgeService.insertReport(reportDto); // bridgeService를 통해 insertReport() 실행 → 신고 기록을 DB에 저장 - insert 성공 시 → 1 반환됨
			if (insertedCount > 0) { // insert 성공하면 아래 로직 실행
				bridgeMapper.plusReportCount(reportedUserId); // 신고당한 사람의 신고 횟수 필드(report_count 등)를 +1 증가
				Map<String, Object> result = new HashMap<>();
				result.put("message", "정상적으로 등록되었습니다.");
				result.put("reportedUserId", reportDto.getReportedUserId());
				result.put("userId", reportDto.getUserId());
				// ↑ 신고자/피신고자 ID 포함 응답으로 줄 JSON 객체를 구성

				return ResponseEntity.status(HttpStatus.OK).body(result);
			} else { // insert 실패 시
				Map<String, Object> result = new HashMap<>();
				result.put("message", "등록된 내용이 없습니다.");
				result.put("count", insertedCount);
				return ResponseEntity.status(HttpStatus.OK).body(result);
				// 신고 insert가 실패했을 때도 예외는 아님 → 200 OK지만 내용 없음이라는 의미
			}
		} catch (Exception e) {
			e.printStackTrace(); // 예외 발생 시 insert나 plusReportCount 도중 오류 발생하면 콘솔에 예외 출력
			Map<String, Object> result = new HashMap<>();
			result.put("message", "등록 중 오류가 발생했습니다.");
			result.put("count", insertedCount);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			// HTTP 500 에러와 함께 실패 메시지 반환
		}
	}

	// --- 메인페이지 하단에 스와이프 동작하는 공지 클릭 시 나타나는 공지 조회 위한 메서드
	@Operation(summary = "공지 목록 조회")
	@GetMapping("/api/announcementList")
	public ResponseEntity<List<AnnouncementDto>> AnnouncementList() throws Exception {
		List<AnnouncementDto> list = bridgeService.announcementList();
		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(list);
		}
	}
	/* AnnouncementList() 메서드는 공지사항 하나하나를 담은 AnnouncementDto 객체들의 리스트를,HTTP 상태 코드와 함께 감싸서 ResponseEntity 형태로 반환함
	 * list에 bridgeService.announcementList() 해당 호출/반환 값을 저장하고
	 * 만약 그 안에 값이 없을 경우 404 상태코드와 함께 null 반환
	 * 있으면 200 OK와 함께 공지목록 반환
	 */

	@Operation(summary = "공지 게시글 조회")
	@GetMapping("/api/announcementDetail/{aIdx}")
	public ResponseEntity<AnnouncementDto> announcementDetail(@PathVariable("aIdx") int aIdx) throws Exception {
		AnnouncementDto announcementDto = bridgeService.announcementDetail(aIdx); // 서비스 호출 <-- 내부에서 DB 쿼리 날려서 공지 데이터 조회
		if (announcementDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(announcementDto);
		}
	}
	// announcementDetail 메서드는 URL 경로의 {aIdx} 값을 정수형 변수 aIdx에 바인딩함
	// aIdx를 인자로 bridgeService.announcementDeatil(aIdx) 메서드 호출 
	// 위의 결과를 AnnouncementDto 타입의 announcementDto에 저장
	// 만약 announcementDto가 null이면 404 상태코드와 함께 null 반환
	// 값이 존재하면 200 OK 상태코드와 함께 해당 공지 데이터를 JSON으로 응답
	
	@Operation(summary = "회원 포인트 충전")
	@GetMapping("/api/chargePoint/{userId}")
	public ResponseEntity<UserDto> chargePonint(@PathVariable("userId") String userId) throws Exception {
		UserDto userDto = bridgeService.chargePoint(userId);
		if (userDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(userDto);
		}
	}
	/* chargePoint() 메서드는 UserDto타입의 객체로 응답.
	 * URL의 {userId} 값을 String 타입의 변수 userId에 바인딩함 
	 * 해당 userId를 인자로 bridgeService.chargePoint(userId)를 호출해 처리 결과를 userDto에 저장
	 * 그 userDto값이 null이면  404 상태코드 및 null 반환
	 * 값 존재하면 OK와 함께 해당 데이터(userDto) JSON으로 응답
	 */
	// *** chargePoint() 메서드는 회원의 포인트를 충전한 후 그 결과를 UserDto 타입으로 응답함
	
	// -----여기도 응답코드에 대한 거 일괄 설덩 및 정리 필요해보이고 
	// + 특히 메서드 네이밍 재정리 필요해보임 공지 Notice랑 Announcement 둘이 중복된듯 보임
	// 왜 그런지 알아보고 그 후 처리 필
}
