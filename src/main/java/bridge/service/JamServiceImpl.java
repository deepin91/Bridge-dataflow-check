package bridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bridge.dto.ConcertDto;
import bridge.dto.ConcertMusicDto;
import bridge.dto.MusicDto;
import bridge.mapper.JamMapper;

@Service
public class JamServiceImpl implements JamService {
	@Autowired
	JamMapper jamMapper;

	@Override
	public int insertJam(ConcertDto concertDto) {
		// TODO Auto-generated method stub
		return jamMapper.insertJam(concertDto);
	}

	@Override
	public int insertMusic(ConcertMusicDto concertMusicDto) {
		// TODO Auto-generated method stub
		return jamMapper.inserMusic(concertMusicDto);
	}

	@Override
	public ConcertDto getJam(int cIdx) {
		// TODO Auto-generated method stub
		return jamMapper.getJam(cIdx);
	}

	@Override
	public List<MusicDto> getMusicUUID(int cIdx) {
		// TODO Auto-generated method stub
		return jamMapper.getMusicUUID(cIdx);
	}

	@Override
	public List<ConcertDto> jamList() {
		// TODO Auto-generated method stub
		return jamMapper.jamList();
	}
}
