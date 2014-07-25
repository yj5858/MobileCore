package com.mobile.sample.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.mobile.core.net.AsyncHttpHandler;
import com.mobile.core.net.FHttpClient;
import com.mobile.core.net.HttpCallBack;

public class MHttpClient {
	
	private static String hostUrl="your host url";
	private static String requestUrl  ="http://"+hostUrl+"/XXXXX/XXX";
	private static MHttpClient instance;
	
	
	public MHttpClient() {
		
	}


	public static MHttpClient getInstance(){
		if(instance!=null){
			return instance;
		}else{
			instance = new MHttpClient();
			return instance;
		}
	}

	
	public  MHttpResult login(Context context,String loginName,String password){
		String url = requestUrl+UrlPath.login;
		Log.i("url",url);
		List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
		nameValues.add(new BasicNameValuePair("loginName",loginName));
		nameValues.add(new BasicNameValuePair("password",password));
		MHttpResult result=(MHttpResult) FHttpClient.getInstance().post(url,nameValues,context,new MHttpResult());
		return result;
	}
	
	public  AsyncHttpHandlerImp<Object> asyncLogin(String loginName,String password,HttpCallBack<Object> callBack){
		String url = requestUrl+UrlPath.login;
		Log.i("url",url);
		List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
		nameValues.add(new BasicNameValuePair("loginName",loginName));
		nameValues.add(new BasicNameValuePair("password",password));
		AsyncHttpHandlerImp<Object> async =(AsyncHttpHandlerImp<Object>) FHttpClient.getInstance().asyncPost(url, nameValues, callBack);
		return async;
	}

}
