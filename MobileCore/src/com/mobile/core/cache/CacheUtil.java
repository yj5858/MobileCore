package com.mobile.core.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CacheUtil {
	public static LinkedHashMap<String, SoftReference<Object>> cacheMap = new LinkedHashMap<String, SoftReference<Object>>();
	/*cache start*/
	public static Object getCache(String key){
		Object cacheObj = cacheMap.get(key);
		return cacheObj;
	}
	
	public static Object removeCache(String key){
		Object cacheObj = cacheMap.remove(key);
		return cacheObj;
	}
	
	public static void clearCache(){
		cacheMap.clear();
	}
	
	public static String setCache(Object cacheObj){
		UUID uid = UUID.randomUUID();
		String idStr = uid.toString();
		setCache(idStr,cacheObj);
		return idStr;
	}
	
	public static void setCache(String key,Object cacheObj){
		SoftReference<Object> referece = new SoftReference<Object>(cacheObj);
		cacheMap.put(key, referece);
	}
	/*cache end*/
}
