package com.huoli.flight.recommend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoli.flight.recommend.bean.RecommendBean;
import com.huoli.flight.recommend.bean.TravelBean;
import com.huoli.flight.recommend.dao.RecommendDao;
import com.huoli.flight.recommend.service.RecommendService;
import com.huoli.flight.recommend.util.DateUtil;
import com.huoli.flight.recommend.util.HttpUtil;
import com.huoli.skydata.beans.AirPort;
import com.huoli.skydata.cache.AirPortCache;
import com.huoli.utils.StringUtil;
import com.travelsky.wap.util.travesky.AirLineData;

@SuppressWarnings("deprecation")
@Service
public class RecommendServiceImpl implements RecommendService {

	private static final Logger logger = LoggerFactory.getLogger(RecommendServiceImpl.class);

	@Autowired
	private RecommendDao recommendDao;

	private static final String SEARCH_URL_PATH = "http://120.133.0.173/flight/ic?st=16&from=flight_recommend";
	private static final String ERR_MSG = "{\"code\":-1,\"msg\":\"无对应航班\"}";
	private static final AirPortCache airPortCache; // 机场数据缓存

	static {
		com.huoli.skydata.CacheData data = new com.huoli.skydata.CacheData();
		data.setLoadFrom(0);
		AirLineData.setCacheData(data);

		airPortCache = AirLineData.getCacheData().getAirPortCache();

	}

	@Override
	public String getRecommendFlight_v2(String orgStation, String dstStation, String date) {
		String org_m = getAirStation(orgStation, 1);// 出发地主机场
		String org_s = getAirStation(orgStation, 2);// 出发地辅机场

		String dst_m = getAirStation(dstStation, 1);// 目的地主机场
		String dst_s = getAirStation(dstStation, 2);// 目的地辅机场

		String res = null;
		try {
			// 查询出发地主机场到目的地主机场
			if (!StringUtil.isNull(org_m) && !StringUtil.isNull(dst_m)) {
				res = getJsonStr(org_m, dst_m, date);
			}
			// 查询出发地主机场到目的地辅机场
			if (ifSearch(res) && !StringUtil.isNull(org_m) && !StringUtil.isNull(dst_s)) {
				res = getJsonStr(org_m, dst_s, date);
			}
			// 查询出发地辅机场到目的地主机场
			if (ifSearch(res) && !StringUtil.isNull(org_s) && !StringUtil.isNull(dst_m)) {
				res = getJsonStr(org_s, dst_m, date);
			}
			// 查询出发地辅机场到目的地辅机场
			if (ifSearch(res) && !StringUtil.isNull(org_s) && !StringUtil.isNull(dst_s)) {
				res = getJsonStr(org_s, dst_s, date);
			}
		} catch (Exception e) {
			logger.error("异常：", e);
		}

		return res;
	}

	private boolean ifSearch(String jsonStr) {
		if (!StringUtil.isNull(jsonStr) && getStatus(jsonStr) != 0) {
			return true;
		}
		return false;
	}

	private Integer getStatus(String jsonStr) {
		JSONObject obj = JSON.parseObject(jsonStr);
		if (obj == null) {
			logger.error("解析json失败！");
			return null;
		}
		Integer status = (Integer) obj.get("status");
		return status;
	}

	private String getJsonStr(String orgStation, String dstStation, String date) throws Exception {
		String url = SEARCH_URL_PATH + "&dst=" + dstStation + "&org=" + orgStation + "&date=" + date;
		String jsonStr = HttpUtil.get(url, 0);
		return jsonStr;
	}

	@Override
	public String getRecommendFlight(String orgStation, String dstStation, String date) {
		String org_m = getAirStation(orgStation, 1);// 出发地主机场
		String org_s = getAirStation(orgStation, 2);// 出发地辅机场

		String dst_m = getAirStation(dstStation, 1);// 目的地主机场
		String dst_s = getAirStation(dstStation, 2);// 目的地辅机场

		RecommendBean bean = null;
		// 查询出发地主机场到目的地主机场
		if (!StringUtil.isNull(org_m) && !StringUtil.isNull(dst_m)) {
			bean = getRecommendBean(org_m, dst_m, date);
		}
		// 查询出发地主机场到目的地辅机场
		if (!hasData(bean) && !StringUtil.isNull(org_m) && !StringUtil.isNull(dst_s)) {
			bean = getRecommendBean(org_m, dst_s, date);
		}
		// 查询出发地辅机场到目的地主机场
		if (!hasData(bean) && !StringUtil.isNull(org_s) && !StringUtil.isNull(dst_m)) {
			bean = getRecommendBean(org_s, dst_m, date);
		}
		// 查询出发地辅机场到目的地辅机场
		if (!hasData(bean) && !StringUtil.isNull(org_s) && !StringUtil.isNull(dst_s)) {
			bean = getRecommendBean(org_s, dst_s, date);
		}

		return bean == null ? ERR_MSG : bean.toString();
	}

	private String getAirStation(String hignStation, int type) {
		TravelBean orgBean = recommendDao.getAirByStation(hignStation);
		if (orgBean == null || (type != 1 && type != 2)) {
			return null;
		}

		String station = type == 1 ? orgBean.getAirport_m() : orgBean.getAirport_s();
		if (StringUtil.isNull(station)) {
			return null;
		}

		return station.contains(",") ? station.substring(0, station.indexOf(",")) : station;
	}

	private RecommendBean getRecommendBean(String org, String dst, String date) {
		try {
			String url = SEARCH_URL_PATH + "&dst=" + dst + "&org=" + org + "&date=" + date;
			String jsonStr = HttpUtil.get(url, 0);
			RecommendBean bean = parseJson(jsonStr);
			// 如果获取的推荐数据是今天，则不加跳转日期
			if (date.equals(bean.getJumpDate())) {
				bean.setJumpDate("");
				bean.setShowDate("");
			}
			return bean;
		} catch (Exception e) {
			logger.error("获取推荐异常：", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private RecommendBean parseJson(String jsonStr) {
		JSONObject obj = JSON.parseObject(jsonStr);
		if (obj == null) {
			logger.error("解析json失败！");
			return null;
		}

		Integer status = (Integer) obj.get("status");
		String msg = (String) obj.get("msg");
		RecommendBean bean = new RecommendBean(status, msg);
		if (status == 0) {// 成功查询到数据
			String date = (String) obj.get("date");

			List<Object> datasList = (List<Object>) obj.get("datas");
			String org = (String) obj.get("org");
			String dst = (String) obj.get("dst");

			Map<String, Map<String, String>> resMap = new HashMap<>();
			for (Object object : datasList) {
				Map<String, String> infoMap = new HashMap<>();

				Map<String, Object> map = (Map<String, Object>) object;
				// 航班信息
				Map<String, Object> flightMap = (Map<String, Object>) map.get("flight_info");
				// 价格信息
				List<Map<String, Object>> priceList = (List<Map<String, Object>>) map.get("price_info");
				if (flightMap != null && flightMap.size() > 0 && priceList != null && priceList.size() > 0) {
					String dacode = (String) flightMap.get("dacode");// 起飞机场三节码
					String aacode = (String) flightMap.get("aacode");// 目的地机场三节码
					String duringminutes = (String) flightMap.get("duringminutes");// 所需时间，分钟
					String fn = (String) flightMap.get("fn");// 班次

					infoMap.put("dacode", dacode);
					infoMap.put("aacode", aacode);
					infoMap.put("duringminutes", duringminutes);
					// 先放入容器中一个价格，与其它价格比较大小
					infoMap.put("price", ((Integer) priceList.get(0).get("price")) + "");

					for (Map<String, Object> priceMap : priceList) {
						Integer price = (Integer) priceMap.get("price");
						// 如果价格小于容器中的价格，则替换掉容器中的值
						if (Integer.valueOf(infoMap.get("price")) > price) {
							infoMap.put("price", price + "");
						}
					}

					resMap.put(fn, infoMap);
				}
			}

			String fn = null;// 航班
			String minPrice = null;// 最低价格
			String minTime = null;// 最短航线

			// 获取该天所有航班的最小价格和航线
			String[] minArr = getMin(resMap);
			if (!StringUtil.isNull(minArr[0]) && !StringUtil.isNull(minArr[1])) {
				fn = minArr[0].split("_")[0];
				minPrice = minArr[0].split("_")[1];
				minTime = minArr[1].split("_")[1];

				// 暂时设定为随机取一个
				bean.setCode(status);
				bean.setMsg(msg);
				bean.setPrice(Integer.valueOf(minPrice));
				bean.setDuringminutes(minTime);
				bean.setDepCode(resMap.get(fn).get("dacode"));
				bean.setArrCode(resMap.get(fn).get("aacode"));
				bean.setDepCityCode(org);
				bean.setArrCityCode(dst);
				bean.setJumpDate(date);
				bean.setShowDate(DateUtil.format(DateUtil.parseDate(date), "MM月dd日"));
				bean.setDepCityName(getCityByCode(resMap.get(fn).get("dacode")));
				bean.setArrCityName(getCityByCode(resMap.get(fn).get("aacode")));
			}
		}
		return bean;
	}

	/**
	 * @param map
	 * @return [fn_minPrice,fn_minTime]最小时间和最小价格并带上各自的班次
	 */
	private String[] getMin(Map<String, Map<String, String>> map) {
		String[] resArr = new String[2];
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			Map<String, String> fnMap = map.get(key);
			String price = fnMap.get("price");
			String duringminutes = fnMap.get("duringminutes");
			if (!StringUtil.isNull(duringminutes)) {
				resArr[0] = key + "_" + price;
				resArr[1] = key + "_" + duringminutes;
				break;
			}
		}

		if (!StringUtil.isNull(resArr[0]) && !StringUtil.isNull(resArr[1])) {
			for (String key : keySet) {
				Map<String, String> fnMap = map.get(key);
				String price = fnMap.get("price");
				String duringminutes = fnMap.get("duringminutes");

				String priceStr = resArr[0];
				String[] arrP = priceStr.split("_");
				if (Integer.valueOf(arrP[1]) > Integer.valueOf(price)) {
					resArr[0] = key + "_" + price;
				}

				String timeStr = resArr[1];
				String[] arrT = timeStr.split("_");
				if (Integer.valueOf(arrT[1]) > Integer.valueOf(duringminutes)) {
					resArr[1] = key + "_" + duringminutes;
				}

			}
		} else {
			logger.error("getMin，原json最小航线或价格数据不存在！");
		}

		return resArr;
	}

	private String getCityByCode(String code) {
		AirPort airport = airPortCache.getAirPortDataByCode(code);
		if (airport != null) {
			return airport.getCityname();
		}
		return null;
	}

	private boolean hasData(RecommendBean bean) {
		if (bean == null || bean.getCode() != 0) {
			return false;
		}
		return true;
	}
}
