package bridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bridge.dto.TipCommentsDto;
import bridge.dto.TipDto;
import bridge.mapper.TipMapper;

@Service
public class TipServiceImpl implements TipService{
	@Autowired
	TipMapper tipMapper;
	
	@Override
	public int insertTip(TipDto tipDto) {
		// TODO Auto-generated method stub
		return tipMapper.insertTip(tipDto);
	}

	@Override
	public TipDto tipdetail(int tbIdx) {
		// TODO Auto-generated method stub
		return tipMapper.tipdetail(tbIdx);
	}

	@Override
	public List<TipCommentsDto> tipcommentslist(int tbIdx) {
		// TODO Auto-generated method stub
		return tipMapper.tipcommentslist(tbIdx);
	}

	@Override
	public List<TipDto> tipList() {
		// TODO Auto-generated method stub
		return tipMapper.tipList();
	}

	@Override
	public void updateViews(int tbIdx) {
		// TODO Auto-generated method stub
		tipMapper.updateViews(tbIdx);
	}

	@Override
	public void insertComment(TipCommentsDto tipCommentsDto) {
		// TODO Auto-generated method stub
		tipMapper.insertComment(tipCommentsDto);
		
	}

	@Override
	public void updateTip(TipDto tipDto) {
		// TODO Auto-generated method stub
		tipMapper.updateTip(tipDto);
	}

	@Override
	public void deleteTip(int tbIdx) {
		tipMapper.deleteTip(tbIdx);
	}

	@Override
	public TipDto selectHeartCount(int tbIdx) {
		// TODO Auto-generated method stub
		return tipMapper.selectHeartCount(tbIdx);
	}

	@Override
	public int updateHeartCount(TipDto tipDto) {
		// TODO Auto-generated method stub
		return tipMapper.updateHeartCount(tipDto);
	}

	@Override
	public int cancleHeartCount(TipDto tipDto) {
		// TODO Auto-generated method stub
		return tipMapper.cancleHeartCount(tipDto);
	}

	@Override
	public List<TipDto> selectHeartsList() {
		// TODO Auto-generated method stub
		return tipMapper.selectHeartsList();
	}
}
//	int updateHeartCount(TipDto tipDto);
//
//}
