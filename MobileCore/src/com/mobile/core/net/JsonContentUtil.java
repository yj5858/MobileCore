package com.mobile.core.net;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.mobile.core.net.HttpCallBack;
import com.mobile.core.util.ObjectMapperProvider;

public  class  JsonContentUtil<T> {
	private static Logger logger = LoggerFactory.getLogger(Log.class);
	private   String code;
	private   String msg;
	private  JsonNode jsonNode;
	private  HttpCallBack<T> callBack;
	private T result;
	private static ObjectMapper mapper = null;
	public JsonContentUtil(HttpCallBack<T> callBack){
		this.callBack = callBack;
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mapper.setDateFormat(df);
		}
	}
	public  void messageConvert(JsonNode jn){
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();
		try {
			this.code = jn.get("code").asText();
			this.msg = jn.get("msg").asText();
			if(!"1".equals(code)){
				if(callBack!=null){
					callBack.onError(code, msg);
				}
			}else{
				if(callBack!=null){
					result = om.readValue(jn.get("result").traverse(), new TypeReference<T>(){});
					callBack.onSuccess(result);
				}
			}
		} catch (JsonProcessingException e) {
			callBack.onError("4", "json数据返回值格式有误,转换失败");
			logger.warn("json数据返回值格式有误,转换失败");
		} catch (IOException e) {
			callBack.onError("5", "json数据返回转换IO错误");
			logger.warn("json数据返回转换IO错误");
		} catch (Exception e){
		}
		
	}
	public static <T> T getEntity(JsonNode jNode,Class<T> clazz) {
		try {
			return mapper.treeToValue(jNode, clazz);
	
		} catch (JsonProcessingException ex) {
			return null;
		}
	}
	
	public static <T> T getEntity(JsonNode jsonNode,TypeReference<T> type) {
		try {
			return mapper.readValue(mapper.treeAsTokens(jsonNode), type);		
		} catch (JsonProcessingException ex) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
