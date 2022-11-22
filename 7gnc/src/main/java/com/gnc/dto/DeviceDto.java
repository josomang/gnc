package com.gnc.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DeviceDto {
	int AR_DEVICE_ID;
	String AR_DEVICE_SN;
	String AR_DEVICE_TYPE;
	String AR_DEVICE_STTS;
	String AR_DEVICE_MEMO;
	LocalDateTime DEL_DT;
}
