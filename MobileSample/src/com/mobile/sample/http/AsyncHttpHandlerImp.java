package com.mobile.sample.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.mobile.core.net.AsyncHttpHandler;
import com.mobile.core.net.FHttpClient;
import com.mobile.core.net.HttpCallBack;
import com.mobile.core.util.ObjectMapperProvider;


public class AsyncHttpHandlerImp<T> implements AsyncHttpHandler<T>{
	private static Logger logger = LoggerFactory.getLogger(AsyncHttpHandlerImp.class);
	static{
		FHttpClient.registHandler(AsyncHttpHandlerImp.class);
	}
	private String code;
	private String msg;
	private T result;
	private HttpCallBack<T> callBack;
	private CountDownLatch count = new CountDownLatch(1);
	public AsyncHttpHandlerImp(HttpCallBack<T> callBack){
		this.callBack = callBack;
	}
	
	private Boolean success = false;
	private Boolean error = false; 
	
	public void handleHttp(InputStream content) {
		ObjectMapper om = ObjectMapperProvider.getObjectMapper();
		
		try {
			JsonNode jn = om.readTree(content);
			this.code = jn.get("code").asText();
			this.msg = jn.get("msg").asText();
			if(!"1".equals(this.code)){
				if(callBack!=null){
					callBack.onError(code, msg);
					error = true;
				}
			}else{
				if(callBack!=null){
					result = om.readValue(jn.get("result").traverse(), new TypeReference<T>(){});
					callBack.onSuccess(result);
					success = true;
				}
			}
		} catch (JsonProcessingException e) {
			callBack.onError("4", "json数据返回值格式有误,转换失败");
			logger.warn("json数据返回值格式有误,转换失败");
			error = true;
		} catch (IOException e) {
			callBack.onError("5", "json数据返回转换IO错误");
			logger.warn("json数据返回转换IO错误");
			error = true;
		} catch (Exception e){
			error = true;
		}
		count.countDown();
	}
	@Override
	public void onError(String code,String msg){
		callBack.onError(code, msg);
		logger.warn(msg);
		error = true;
		count.countDown();
	}
	
	
	public Boolean isComplete(){
		return success||error;
	}
	@Override
	public Boolean isSuccess() {
		return success;
	}
	@Override
	public Boolean isError() {
		return error;
	}
	@Override
	public void waitForComplete() {
		try {
			count.await();
		} catch (InterruptedException e) {
		}
	}
	@Override
	public T getResult() {
		return result;
	}

}
