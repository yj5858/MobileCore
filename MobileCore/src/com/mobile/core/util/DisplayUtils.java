package com.mobile.core.util;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public class DisplayUtils {
	  /**
     * Get pixels from dps
     *
     * @param view
     * @param dp
     * @return pixels
     */
    public static float getPixels(final View view, final int dp) {
        return getPixels(view.getResources(), dp);
    }

    /**
     * Get pixels from dps
     *
     * @param resources
     * @param dp
     * @return pixels
     */
    public static float getPixels(final Resources resources, final int dp) {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
    }

    /**
     * Get pixels from dps
     *
     * @param view
     * @param dp
     * @return pixels
     */
    public static int getIntPixels(final View view, final int dp) {
        return getIntPixels(view.getResources(), dp);
    }

    /**
     * Get pixels from dps
     *
     * @param context
     * @param dp
     * @return pixels
     */
    public static int getIntPixels(final Context context, final int dp) {
        return getIntPixels(context.getResources(), dp);
    }

    /**
     * Get pixels from dps
     *
     * @param resources
     * @param dp
     * @return pixels
     */
    public static int getIntPixels(final Resources resources, final int dp) {
        float pixels = TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
        return (int) Math.floor(pixels + 0.5F);
    }
    
    /** 
     * 从 px(像素) 的单位 转成为 dp 
     */  
	public static  int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f)-15;  
    }  

}
