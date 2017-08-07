package com.huoli.flight.recommend.dao;

import com.huoli.flight.recommend.bean.TravelBean;

public interface RecommendDao {
	/**
	 * 根据高铁站三节码返回机场三节码
	 * 
	 * @param station
	 * @return
	 */
	TravelBean getAirByStation(String station);
}
