package com.gnc.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CenterDto {
	int num;
	String CENTER_ID;
	String CENTER_TTL;
	String CENTER_UID;
	String CENTER_PSWD;
	int CENTER_MAX_NOPE;
	Date REG_DT;
	Date UPDT_DT;
	int NOPE;
	
}
