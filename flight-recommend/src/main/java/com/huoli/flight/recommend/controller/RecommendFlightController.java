package com.huoli.flight.recommend.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huoli.flight.recommend.service.RecommendService;
import com.huoli.utils.StringUtil;

/**
 * @author liangkc 获取推荐航班
 */
@Controller
@RequestMapping("/recommendFlight")
public class RecommendFlightController {

	@Autowired
	private RecommendService recommendService;

	/**
	 * 根据出发地和目的地获取推荐航班
	 * 
	 * @param orgStation
	 *            出发城市高铁站
	 * @param dstStation
	 *            目的地城市高铁站
	 * @param date
	 *            出发日期
	 * @return
	 */
	@RequestMapping(value = "/get", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String getRecommendFlight(@RequestParam String orgStation, @RequestParam String dstStation,
			@RequestParam String date, HttpServletResponse response) {

		String res = null;
		PrintWriter pw = null;
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			res = recommendService.getRecommendFlight_v2(orgStation, dstStation, date);
			if (!StringUtil.isNull(res)) {
				pw.write(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		return null;
	}
}
