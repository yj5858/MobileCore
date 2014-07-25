package com.mobile.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Provides;

public class ObjectMapperProvider {
	
	private static ObjectMapper om;
	static{
		om = new ObjectMapper();
		om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		om.setDateFormat(df);
	}
	
	@Provides
	public static ObjectMapper getObjectMapper(){
		return om;
	}
}
 