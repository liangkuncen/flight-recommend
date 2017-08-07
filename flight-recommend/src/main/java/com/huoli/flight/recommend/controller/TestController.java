package com.huoli.flight.recommend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huoli.flight.recommend.bean.TravelBean;
import com.huoli.flight.recommend.service.TravelService;

@RestController
@RequestMapping("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	private TravelService travelService;

    @Value("${app.name}")
    private String projectName;

    @RequestMapping("/health")
    public String fine() {
    	logger.error("error信息");
        return String.format("%s fine", projectName);
    }
    
    @RequestMapping("/query")
    public String queryList(){
    	List<TravelBean> resList = travelService.queryList();
    	if(resList !=null && resList.size() > 0){
    		return resList.get(0).toString();
    	}
    	
    	return "数据获取失败";
    }
}
