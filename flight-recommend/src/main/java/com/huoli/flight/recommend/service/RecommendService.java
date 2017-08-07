package com.huoli.flight.recommend.service;

public interface RecommendService {
	String getRecommendFlight(String orgSta, String dstSta, String date) throws Exception;
	
	String getRecommendFlight_v2(String orgSta, String dstSta, String date) throws Exception;
}
