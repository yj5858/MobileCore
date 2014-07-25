package com.mobile.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class FileUtil {
	/**
	 * 取得文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	
public final static String SERVER_URL_FILE_NAME="serverUrl";
	

	public static String readServerUrl(Context context,String fileName){
    	String serverUrl = null;
    	FileInputStream fis = null;
    	
    	try {
    		fis = context.openFileInput (fileName);
			int length = fis.available();
			byte[] b = new byte[length];
			fis.read(b);
			serverUrl = EncodingUtils.getString(b, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return serverUrl;
    }
    
    public static void writeServerUrl(Context context,String text,String fileName){
    	FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(text.getBytes());
			fos.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Boolean isExistFile(Context context){
    	Boolean flag = false;
    	for(int i=0;i<context.fileList().length;i++){
    		if(SERVER_URL_FILE_NAME.equals(context.fileList()[i])){
    			flag = true;
    		}
    	}
    	return flag;    	
    }
}
