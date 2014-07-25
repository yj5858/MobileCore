package com.mobile.sample.http;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.core.net.FHttpResult;
import com.mobile.sample.R;


public class MHttpResult implements FHttpResult {

	private String url = null;
	private List<NameValuePair> paramPairs = null;

	private static ObjectMapper mapper = null;
	
	public MHttpResult() {
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mapper.setDateFormat(df);
		}
	}
	
	public MHttpResult(Exception ex) {
		this.setError(ex.getMessage());
	}

	public String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	public List<NameValuePair> getParamPairs() {
		return paramPairs;
	}

	protected void setParamPairs(List<NameValuePair> paramPairs) {
		this.paramPairs = paramPairs;
	}
	//系统换行符
	private static final String NEWLINE = System.getProperties().getProperty("line.separator");

	private List<String> errors = null;
	@Override
	public String getError() {
		if (errors == null || errors.size() == 0) {
			return null;
		} else if (errors.size() == 1) {
			return errors.get(0);
		} else {
			StringBuffer context = new StringBuffer();
			for (String item : errors) {
				context.append(item).append(NEWLINE);
			}
			return context.toString();
		}
	}
	@Override
	public void setError(String error) {
		if(error == null) error = "Unknown Error";
		success = false;
		if (errors == null) errors = new ArrayList<String>();
		this.errors.add(error);
		Log.i("FHttpResult", error);
	}
	
	public void toastError(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.alert_error, null);
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("提示!")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(view)
				.setNegativeButton("关闭", null)
				.create();
		((TextView)view.findViewById(R.id.alerterror_content)).setText(this.getError());
		dialog.show();
		
	}
	
	private boolean success = true;
	@Override
	public boolean isSuccess() {
		return success;
	}

	private String code = null;
	private String msg = null;
	private JsonNode resultNode = null;

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public JsonNode getResultNode() {
		return resultNode;
	}

	private String reLoginFlag = null;
	
	public String getReLoginFlag() {
		return reLoginFlag;
	}

	public void setReLoginFlag(String reLoginFlag) {
		this.reLoginFlag = reLoginFlag;
	}
	
	private String entityInfo = null;
	
	public String getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(String entityInfo) {
		this.entityInfo = entityInfo;
	}

	@Override
	public void setResponse(String entityinfo) throws JsonProcessingException, IOException {
		Log.i("MHttpResult", entityinfo);
		
		JsonNode jNode = mapper.readTree(entityinfo);
		this.code = jNode.get("code").asText();
		this.msg = jNode.get("msg").asText();
		if(!"1".equals(this.code)) this.setError(this.msg);
		this.resultNode = jNode.get("result");
				
	}


	public static <T> T getEntity(JsonNode jNode,Class<T> clazz) {
		try {
			return mapper.treeToValue(jNode, clazz);
	
		} catch (JsonProcessingException ex) {
			//this.setError("HTTP请求中解析返回数据时发生错误:" + ex.getMessage());
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
	 
//	public static <T> T getDataList(JsonNode jsonNode,TypeReference<T> type) {
//		return getEntity(jsonNode.get("dataList"), type);
//	}
}