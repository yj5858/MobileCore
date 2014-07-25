package com.mobile.core.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


public class MobileApplicationContext {
	private static MobileApplicationContext mobileContext  = new MobileApplicationContext();

	
	private MobileApplicationContext(){
	}
	
	public static MobileApplicationContext getInstance(){
		return mobileContext;
	}
	
	
}
