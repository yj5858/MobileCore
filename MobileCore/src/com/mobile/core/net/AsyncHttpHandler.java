package com.mobile.core.net;

import java.io.InputStream;


public interface AsyncHttpHandler<T>{
	void handleHttp(InputStream content);
	Boolean isComplete();
	Boolean isSuccess();
	Boolean isError();
	T getResult();
	void onError(String code,String msg);
	void waitForComplete();
}
