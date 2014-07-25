package com.mobile.core.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


public class MobileApplicationContext {
	private static MobileApplicationContext mobileContext  = new MobileApplicationContext();
	private Map<String, Object> cacheMap = new LinkedHashMap<String, Object>();
	
	private MobileApplicationContext(){
	}
	
	public static MobileApplicationContext getInstance(){
		return mobileContext;
	}
	
	/*cache start*/
	public Object getCache(String key){
		Object cacheObj = cacheMap.get(key);
		return cacheObj;
	}
	
	public Object removeCache(String key){
		Object cacheObj = cacheMap.remove(key);
		return cacheObj;
	}
	
	private void clearCache(){
		cacheMap.clear();
	}
	
	public String setCache(Object cacheObj){
		UUID uid = UUID.randomUUID();
		String idStr = uid.toString();
		setCache(idStr,cacheObj);
		return idStr;
	}
	
	public void setCache(String key,Object cacheObj){
		cacheMap.put(key, cacheObj);
	}
	/*cache end*/
}
