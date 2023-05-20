package com.gnc.dto;





import lombok.Data;

@Data
public class LessonsDto {
	private int LESSON_ID;
	private int LESSON_CENTER_ID;
	private String LESSON_TTL;
	private String ROOM_ID;
	private String INSTR_NM;
	private String LESSON_TYPE;
	private String LESSON_DESC;
	private String BGNG_DT;
	private String END_DT ;
	private int LESSON_TM;
	private int NOPE;
	private int center;
	private int DGSTFN_SCORE;
	
}