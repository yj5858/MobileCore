package com.mobile.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.mobile.core.R;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomException implements UncaughtExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(CustomException.class);
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	// 获取application 对象；
	private Context mContext;

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	
	// 单例声明CustomException;
	private static CustomException customException;

	private CustomException() {
	}

	public static CustomException getInstance() {
		if (customException == null) {
			customException = new CustomException();
		}
		return customException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		if (!handleException(exception) && defaultExceptionHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			defaultExceptionHandler.uncaughtException(thread, exception);
		} else {
			// Sleep一会后结束程序
			// 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e("CustomException", "uncaughtException >>>>>>>" + exception.getLocalizedMessage());
			}
			Log.e("CustomException", "uncaughtException >>>>>>>" + exception.getLocalizedMessage());
			android.os.Process.killProcess(android.os.Process.myPid());
			 System.exit(0);
			//使用退出工具退出，killProcess方法经过测试退出的是当前的activity
			//ExitUtil.exit();
		}

	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		ex.printStackTrace();
		
		final String msg = ex.getLocalizedMessage();

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				// Toast 显示需要出现在一个线程的消息队列中
				Looper.prepare();
				Toast.makeText(mContext, "程序出错即将退出！" + msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		// 收集设备信息
		//Calendar ca = Calendar.getInstance(Locale.CHINA);
		StringBuffer sb = new StringBuffer();
		if(ex.getStackTrace()!=null&&ex.getStackTrace().length!=0){
			
		 	StringWriter mStringWriter = new StringWriter();  
	        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);  
	        ex.printStackTrace(mPrintWriter);  
	        mPrintWriter.close();  
	        String stackTrace = mStringWriter.toString();
	        Log.e("exception collect", stackTrace); 
	        sb.append(stackTrace);
	        
		}
		
		logger.debug("时间："+dateFormat.format(new Date())+"\n"+"异常："+ex+"\n"+"异常堆栈："+sb);
		
		
		return true;
	}

	public void init(Context context) {
		mContext = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		PropertyConfigurator.getConfigurator(mContext).configure();
		final FileAppender  fa =  (FileAppender) logger.getAppender(1);   
		fa.setAppend(true); 
	}

    public static void alertError(Context context, Throwable ex) {
		View view = LayoutInflater.from(context).inflate(R.layout.alert_error, null);
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("系统发生异常")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(view)
				.setNegativeButton("关闭", null)
				.create();
		((TextView)view.findViewById(R.id.alerterror_content)).setText(ex.getMessage());
		dialog.show();
    }
}
