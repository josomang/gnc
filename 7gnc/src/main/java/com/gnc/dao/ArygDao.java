package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.Criteria2;

@Mapper
public interface ArygDao {
	public List<String> arygListDao(Criteria2 cri);
	
	public List<String> arygSearchListDao(@Param("year")String year,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	
	public int getSearchCount (@Param("year")String year);
	
	public Integer avgSearchDao(@Param("year")String year);
}
