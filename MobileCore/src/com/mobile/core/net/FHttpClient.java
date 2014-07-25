package com.mobile.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.core.util.ObjectMapperProvider;


public class FHttpClient {
	private DefaultHttpClient client;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	private Class<? extends AsyncHttpHandler> handlerClass;
	private static FHttpClient instance = new FHttpClient();
	private ObjectMapper om = ObjectMapperProvider.getObjectMapper();

	public FHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 15000);
		HttpConnectionParams.setSoTimeout(params, 15000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, false);
		HttpProtocolParams.setUserAgent(params, "mobileClient");
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));
		this.client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});
		client.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							BasicHttpEntity convertEntity = new BasicHttpEntity();
							try {
								convertEntity.setContent(new GZIPInputStream(response.getEntity().getContent()));
							} catch (IOException e) {
								e.printStackTrace();
							}
							response.setEntity(convertEntity);
							break;
						}
					}
				}
			}
		});
	}

	public static void addResponseInterceptor(HttpResponseInterceptor ri) {
		instance.client.addResponseInterceptor(ri);
	}

	public static void addResponseInterceptor(HttpResponseInterceptor ri, int index) {
		instance.client.addResponseInterceptor(ri, index);
	}

	public static void addRequestInterceptor(HttpRequestInterceptor ri) {
		instance.client.addRequestInterceptor(ri);
	}

	public static void addRequestInterceptor(HttpRequestInterceptor ri, int index) {
		instance.client.addRequestInterceptor(ri, index);
	}

	public static FHttpClient getInstance() {
		return instance;
	}

	public static void registHandler(Class<? extends AsyncHttpHandler> handlerClass) {
		instance.handlerClass = handlerClass;
	}

	public void setUserAgent(String ua) {
		client.getParams().setParameter(HTTP.USER_AGENT, ua);
	}

	public <T> AsyncHttpHandler<T> asyncPost(final String uri, final List<NameValuePair> params, HttpCallBack<T> callback) {

		AsyncHttpHandler<T> tempHttpHandler = null;
		try {
			if (callback == null) {
				callback = new EmptyHttpCallBack<T>();
			}
			tempHttpHandler = handlerClass.getConstructor(HttpCallBack.class).newInstance(callback);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		final AsyncHttpHandler<T> httpHandler = tempHttpHandler;
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpPost httpPost = createPost(uri, params);
					HttpResponse rep = client.execute(httpPost);
					if (rep != null) {
						int statusCode = rep.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							httpHandler.handleHttp(rep.getEntity().getContent());
							// T t = om.readValue(rep.getEntity().getContent(),
							// new
							// TypeReference<T>(){});
							// callback.onSuccess(t);
						} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
							httpHandler.onError("1", "服务内部异常");
						} else {
							httpHandler.onError("2", "服务内部异常");
						}
					}

				} catch (IOException e) {
					httpHandler.onError("3", "网络出现异常,请求失败");
				}
			}
		});
		th.start();
		
//		new AsyncTask<Object , Void, HttpResponse>() {
//
//			@Override
//			protected HttpResponse doInBackground(Object ... params) {
//				
//				
//				HttpPost httpPost = null;
//	        	HttpResponse rep = null;
//				try {
//					httpPost = createPost((String)params[0], (List<NameValuePair>)params[1]);
//					rep = client.execute(httpPost);
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//					
//					return rep;
//					
//			}
//
//			@Override
//			protected void onProgressUpdate(Void... values) {
//				
//			}
//
//			@Override
//			protected void onPostExecute(HttpResponse rep) {
//				try{
//				if (rep != null) {
//					int statusCode = rep.getStatusLine().getStatusCode();
//					if (statusCode == HttpStatus.SC_OK) {
//						httpHandler.handleHttp(rep.getEntity().getContent());
//						
//					} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//						httpHandler.onError("1", "服务内部异常");
//					} else {
//						httpHandler.onError("2", "服务内部异常");
//					}
//				}
//
//			} catch (IOException e) {
//				httpHandler.onError("3", "网络出现异常,请求失败");
//			}
//			
//			
//				
//		}}.execute(uri, params);
//		
		
		return httpHandler;
	}

//	public <T> T post(final String uri,Context context, final Object params, FHttpResult result) throws IOException {
//		return post(uri, params, null,context,result);
//	}

	public FHttpResult post(final String uri, final List<NameValuePair> params,  Context context, FHttpResult result)
	{
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if (!wifi && !internet) {
			result.setError("未开启设备的网络连接！");
			return result;
		}
		try {
			HttpPost httpPost = createPost(uri, params);
			HttpResponse rep = client.execute(httpPost);

			if (rep != null) {
				if (rep.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					result.setError("网络请求时发生错误：请求失败！");
				} else {
//					if (type != null) {
//						return om.readValue(rep.getEntity().getContent(), type);
//					}
					result.setResponse(EntityUtils.toString(rep.getEntity()));
				}
			}
		} catch (ConnectTimeoutException ex) {
			result.setError("网络连接超时！");
		} catch (UnsupportedEncodingException ex) {
			result.setError("网络请求中转换参数编码格式时发生错误：" + ex.getMessage());
		} catch (ClientProtocolException ex) {
			result.setError("网络请求时发生错误：" + ex.getMessage());
		} catch (IOException ex) {
			result.setError("网络请求时发生错误:服务器连接失败,请检查地址设置.");
		}
		return result;

	}

	public HttpPost createPostByJson(final String uri, final Object params) throws IOException {
		HttpPost httpRequest = new HttpPost(uri);
		httpRequest.setHeader("Content-Type", "application/json");
		// BasicHttpEntity entity =new BasicHttpEntity();
		if (params != null) {
			byte[] bs = om.writeValueAsBytes(params);
			ByteArrayEntity entityData = new ByteArrayEntity(bs);
			httpRequest.setEntity(entityData);
		}
		return httpRequest;
	}
	
	public HttpPost createPost(final String uri, final List<NameValuePair> params) throws IOException {
		HttpPost httpRequest = new HttpPost(uri);
		//httpRequest.setHeader("Content-Type", "application/json");
		if (params != null) {
			UrlEncodedFormEntity entityData = new UrlEncodedFormEntity(params, "UTF-8");
			httpRequest.setEntity(entityData);
		}		
		return httpRequest;
	}

}
