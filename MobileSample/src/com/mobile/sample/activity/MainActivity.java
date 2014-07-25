package com.mobile.sample.activity;



import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.mobile.sample.R;
import com.mobile.sample.http.MHttpClient;
import com.mobile.sample.http.MHttpResult;

public class MainActivity extends Activity {

	
	private EditText userName;
	private EditText password;
	private Button login;
	private Handler mHandler;
	private Intent intent;
	private Context context;
	private MHttpResult httpResult;
	public Handler getHandler(){
		return mHandler;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
       
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        
        
        
        
        
        mHandler = new Handler(Looper.getMainLooper()){
        	@Override
        	public void handleMessage(Message msg) {
        		// TODO Auto-generated method stub
        		super.handleMessage(msg);

                  httpResult =(MHttpResult)msg.obj;
        		if (httpResult.isSuccess()) {
        		//LoginInfo loginInfo = MHttpResult.getEntity(httpResult.getResultNode().get("loginResult"), LoginInfo.class);
				intent = new Intent(context,NextActivity.class);
     			startActivity(intent);
				} else {
				
					httpResult.toastError(context);
				}
   

        	}
        };
        login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("".equals(userName.getText().toString().trim())){
					Toast.makeText(context, "用户名不能为空！!", Toast.LENGTH_LONG).show();
				}else{
					new Thread(){
						public void run() {
							
							httpResult = MHttpClient.getInstance().login(context, userName.getText().toString(), password.getText().toString());
							
							
							Message msg = mHandler.obtainMessage(); 
							msg.obj = httpResult;
							mHandler.sendMessage(msg);
						};
					}.start();
//					httpClient = new MHttpClient(context);
//					AsyncHttpHandler<Object> async =  httpClient.asyncLogin( userName.getText().toString(), password.getText().toString(),new HttpCallBack<Object>() {
//
//						@Override
//						public void onSuccess(Object result) {
//							
//						}
//
//						@Override
//						public void onError(String code, String msg) {
//							
//						}
//					});
//					async.waitForComplete();
//					async.getResult();
					
					
				}
			}
		});
        
 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
   
    }
