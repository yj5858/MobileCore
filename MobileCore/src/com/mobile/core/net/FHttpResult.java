package com.mobile.core.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;


public interface FHttpResult {

	
	public void setResponse(String entityinfo)throws JsonProcessingException, IOException;
	
	
	public boolean isSuccess();
	
	public String getError();

	public void setError(String error);

	
}