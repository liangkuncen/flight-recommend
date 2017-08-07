package com.huoli.flight.recommend.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;


public class TravelBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String station;
	private String airport_m;
	private String airport_s;
	private Integer status;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getAirport_m() {
		return airport_m;
	}
	public void setAirport_m(String airport_m) {
		this.airport_m = airport_m;
	}
	public String getAirport_s() {
		return airport_s;
	}
	public void setAirport_s(String airport_s) {
		this.airport_s = airport_s;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		
		return JSON.toJSONString(this);
	}
	
}
