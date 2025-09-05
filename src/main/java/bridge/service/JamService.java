package bridge.service;

import java.util.List;

import bridge.dto.ConcertDto;
import bridge.dto.ConcertMusicDto;
import bridge.dto.MusicDto;

public interface JamService {

	int insertJam(ConcertDto concertDto);

	int insertMusic(ConcertMusicDto concertMusicDto);

	ConcertDto getJam(int cIdx);

	List<MusicDto> getMusicUUID(int cIdx);

	List<ConcertDto> jamList();

}
