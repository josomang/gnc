package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.Criteria;
import com.gnc.dto.DeviceDto;
import com.gnc.dto.PageDTO;

@Mapper
public interface DeviceDao {
	public void registerDao(String number, String type,
			String condition,String memo);
	
	public void updateDao(@Param("a")String a,@Param("b")String b,
			@Param("c")String c,@Param("d")String d);
	
	public List<DeviceDto> deviceDao(@Param("a")Object a);
	
	public List<String> deviceListDao(Criteria cri,PageDTO pageDTO,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	public int getCount();
	
	public int getSearchCount (@Param("keyword")Object keyword);
	
	public List<String> searchDao(@Param("keyword")Object keyword,PageDTO pageDTO,@Param("pageStart") int start,@Param("perPageNum")int perPageNum,Criteria cri);
	
	public void deleteDao(@Param("a")String number);
	
	
}
