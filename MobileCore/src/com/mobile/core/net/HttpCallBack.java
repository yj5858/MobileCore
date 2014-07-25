package com.mobile.core.net;

public interface HttpCallBack<T> {
	
	void onSuccess(T t);
	void onError(String code,String msg);
	
}
