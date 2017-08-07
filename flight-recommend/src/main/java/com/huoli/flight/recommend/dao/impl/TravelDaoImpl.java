package com.huoli.flight.recommend.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.huoli.flight.recommend.bean.TravelBean;
import com.huoli.flight.recommend.dao.TravelDao;

@Repository
public class TravelDaoImpl implements TravelDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public static final RowMapper<TravelBean> TRAVEL_BEAN_ROW_MAPPER =
            (rs, i) -> {
            	TravelBean bean = new TravelBean();
            	bean.setId(rs.getInt("id"));
                bean.setStation(rs.getString("station"));
                bean.setAirport_m(rs.getString("airport_m"));
                bean.setAirport_s(rs.getString("airport_s"));
                bean.setStatus(rs.getInt("status"));
				return bean;
            };
	
	@Override
	public List<TravelBean> queryList() {
		String sql = "select * from TICKET_HIGHGAIL_AIRPORT limit 1";
		return jdbcTemplate.query(sql, TRAVEL_BEAN_ROW_MAPPER);
	}

}
