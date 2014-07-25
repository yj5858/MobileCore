package com.mobile.core.net;

import com.mobile.core.net.HttpCallBack;

public class EmptyHttpCallBack<T> implements HttpCallBack<T> {

	@Override
	public void onSuccess(T t) {
	}

	@Override
	public void onError(String code, String msg) {
	}
}
