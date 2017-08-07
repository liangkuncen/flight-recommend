package com.huoli.flight.recommend.dao;

import java.util.List;

import com.huoli.flight.recommend.bean.TravelBean;

public interface TravelDao {
	List<TravelBean> queryList();
}
