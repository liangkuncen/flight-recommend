package com.huoli.flight.recommend.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class RecommendBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer code;
	private String msg;
	private String depCode;// 出发站点机场三字码
	private String arrCode;// 到达站点机场三字码
	private String depCityCode;// 出发站点城市编码
	private String arrCityCode;// 到达站点城市编码
	private String depCityName;// 出发站点城市名称
	private String arrCityName;// 到达站点城市名称
	private Integer price;// 当前航线最低价
	private String duringminutes;// 最短航线
	private String jumpDate;// 不是今天的航班时的跳转日期
	private String showDate;// 不是今天的航班时的显示日期

	public RecommendBean() {
		super();
	}

	public RecommendBean(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDepCode() {
		return depCode;
	}

	public void setDepCode(String depCode) {
		this.depCode = depCode;
	}

	public String getArrCode() {
		return arrCode;
	}

	public void setArrCode(String arrCode) {
		this.arrCode = arrCode;
	}

	public String getDepCityCode() {
		return depCityCode;
	}

	public void setDepCityCode(String depCityCode) {
		this.depCityCode = depCityCode;
	}

	public String getArrCityCode() {
		return arrCityCode;
	}

	public void setArrCityCode(String arrCityCode) {
		this.arrCityCode = arrCityCode;
	}

	public String getDepCityName() {
		return depCityName;
	}

	public void setDepCityName(String depCityName) {
		this.depCityName = depCityName;
	}

	public String getArrCityName() {
		return arrCityName;
	}

	public void setArrCityName(String arrCityName) {
		this.arrCityName = arrCityName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getDuringminutes() {
		return duringminutes;
	}

	public void setDuringminutes(String duringminutes) {
		this.duringminutes = duringminutes;
	}

	public String getJumpDate() {
		return jumpDate;
	}

	public void setJumpDate(String jumpDate) {
		this.jumpDate = jumpDate;
	}

	public String getShowDate() {
		return showDate;
	}

	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
