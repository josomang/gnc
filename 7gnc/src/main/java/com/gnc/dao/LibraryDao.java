package com.gnc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LibraryDao {
	public Integer getLibraryPeopleDao(@Param("REG_DT")String REG_DT);
}
