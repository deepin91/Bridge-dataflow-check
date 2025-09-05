package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.ConcertDto;
import bridge.dto.ConcertMusicDto;
import bridge.dto.MusicDto;

@Mapper
public interface JamMapper {

	int insertJam(ConcertDto concertDto);

	int inserMusic(ConcertMusicDto concertMusicDto);

	ConcertDto getJam(int cIdx);

	List<MusicDto> getMusicUUID(int cIdx);

	List<ConcertDto> jamList();

}
