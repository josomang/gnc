package com.gnc.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gnc.dao.ArDao;
import com.gnc.dao.CenterDao;
import com.gnc.dao.DeviceDao;
import com.gnc.dao.LessonsDao;
import com.gnc.dao.LibraryDao;
import com.gnc.dao.UserDao;
import com.google.gson.JsonObject;

@Service
public class kpiService {
	@Autowired
	UserDao userDao;

	@Autowired
	LessonsDao lessonsDao;

	@Autowired
	CenterDao centerDao;

	@Autowired
	ArDao arDao;

	@Autowired
	LibraryDao libraryDao;
	
	@Autowired
	DeviceDao deviceDao;
	
//@Scheduled(cron = "0 30 22 31 12 ?")
public String kpi(){
		
		LocalDateTime now = LocalDateTime.now();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		int increase= Integer.parseInt(year)-1;
		String in= Integer.toString(increase);
		Integer a ,b,c,d,e;
		if(arDao.arStatisticsSum(year)!=null&&arDao.arStatisticsSum(in)!=null) {
			a=arDao.arStatisticsSum(year)-arDao.arStatisticsSum(in);
		}
		else {
			a=null;
		}
		
		if(centerDao.centerStatisticsSum(year)!=null&&centerDao.centerStatisticsSum(in)!=null) {
			b=centerDao.centerStatisticsSum(year)-centerDao.centerStatisticsSum(in);
		}
		else {
			b=null;
		}
		
		if(libraryDao.getLibraryPeopleDao(year)!=null&&libraryDao.getLibraryPeopleDao(in)!=null) {
			c=libraryDao.getLibraryPeopleDao(year)-libraryDao.getLibraryPeopleDao(in);
		}
		else {
			c=null;
			
		}
		
		if(arDao.arUseStatisticsSum(56*21*12,year)!=null&&arDao.arUseStatisticsSum(56*21*12,in)!=null) {
			d=arDao.arUseStatisticsSum(56*21*12,year)-arDao.arUseStatisticsSum(56*21*12,in);
		}
		else {
			d=null;
			
		}
		if(centerDao.centerUseStatisticsSum(year)!=null&&centerDao.centerUseStatisticsSum(in)!=null) {
			e=centerDao.centerUseStatisticsSum(year)-centerDao.centerUseStatisticsSum(in);
		}
		else {
			e=null;
			
		}
		
		
		 
		/*
		 * HashMap<String,Integer> map = new HashMap<>(); HashMap<String,Integer> map1 =
		 * new HashMap<>(); HashMap<String,Integer> map2 = new HashMap<>();
		 * HashMap<String,Integer> map3 = new HashMap<>(); HashMap<String,Integer> map4
		 * = new HashMap<>(); HashMap<String,Integer> map5 = new HashMap<>();
		 */
		
		/*
		 * HashMap<String,HashMap<String,Integer>> kpi = new HashMap<>();
		 * 
		 * map.put("year", map.getOrDefault("year",0)+Integer.parseInt(year));
		 * 
		 * map1.put("target", map1.getOrDefault("target",0)+90); map1.put("result",
		 * map1.getOrDefault("result",0)+85); map1.put("increase_rate",
		 * map1.getOrDefault("increase_rate",0)+10);
		 * 
		 * map2.put("target", map2.getOrDefault("target",0)+90); map2.put("result",
		 * map2.getOrDefault("result",0)+85); map2.put("increase_rate",
		 * map2.getOrDefault("increase_rate",0)+10);
		 * 
		 * map3.put("target", map3.getOrDefault("target",0)+90); map3.put("result",
		 * map3.getOrDefault("result",0)+85); map3.put("increase_rate",
		 * map3.getOrDefault("increase_rate",0)+10);
		 * 
		 * map4.put("target", map4.getOrDefault("target",0)+90); map4.put("result",
		 * map4.getOrDefault("result",0)+85); map4.put("increase_rate",
		 * map4.getOrDefault("increase_rate",0)+10);
		 * 
		 * map5.put("target", map5.getOrDefault("target",0)+90); map5.put("result",
		 * map5.getOrDefault("result",0)+85); map5.put("increase_rate",
		 * map5.getOrDefault("increase_rate",0)+10);
		 */
		
		JsonObject obj =new JsonObject();
		 obj.addProperty("year", Integer.parseInt(year));
		 
		 JsonObject data1 = new JsonObject();
		 data1.addProperty("target", userDao.arStatisticsSum(year));
		 data1.addProperty("result", arDao.arStatisticsSum(year));
		 data1.addProperty("increase_rate",a);
		 obj.add("ar_dgstfn", data1);
		 
		 JsonObject data2 = new JsonObject();
		 data2.addProperty("target", userDao.centerStatisticsSum(year));
		 data2.addProperty("result", centerDao.centerStatisticsSum(year));
		 data2.addProperty("increase_rate", b);
		 obj.add("class_dgstfn", data2);
		 
		 JsonObject data3 = new JsonObject();
		 data3.addProperty("target", userDao.getLibraryPeopleDao(year));
		 data3.addProperty("result", libraryDao.getLibraryPeopleDao(year));
		 data3.addProperty("increase_rate",c);
		 obj.add("library_utztn_count", data3);
		 
		 JsonObject data4 = new JsonObject();
		 data4.addProperty("target", userDao.arUseStatisticsSum(year));
		 data4.addProperty("result", arDao.arUseStatisticsSum(56*21*12,year));
		 data4.addProperty("increase_rate", d);
		 obj.add("ar_utztn_rate", data4);
		
		 JsonObject data5 = new JsonObject();
		 data5.addProperty("target", userDao.centerUseStatisticsSum(year));
		 data5.addProperty("result", centerDao.centerUseStatisticsSum(year));
		 data5.addProperty("increase_rate",e);
		 obj.add("class_utztn_rate", data5);
		
		  
		 // RestTemplate 객체 생성
		 RestTemplate restTemplate = new RestTemplate();
		
		 
			/*
			 * kpi.put("year", map); kpi.put("ar_dgstfn", map1); kpi.put("class_dgstfn",
			 * map2); kpi.put("library_utztn_count", map3); kpi.put("ar_utztn_rate", map4);
			 * kpi.put("class_utztn_rate", map5);
			 */
		 ResponseEntity<String> response = restTemplate.exchange("http://59.26.190.76:8080/dd", HttpMethod.POST,new HttpEntity<String>( obj.toString()), String.class);
		
		 
		

		return obj.toString();
	}
	
}