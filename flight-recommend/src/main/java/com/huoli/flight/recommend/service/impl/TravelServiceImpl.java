package com.huoli.flight.recommend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huoli.flight.recommend.bean.TravelBean;
import com.huoli.flight.recommend.dao.TravelDao;
import com.huoli.flight.recommend.service.TravelService;

@Service
public class TravelServiceImpl implements TravelService {

	@Autowired
	private TravelDao travelDao;
	
	@Override
	public List<TravelBean> queryList() {
		
		return travelDao.queryList();
	}

}
