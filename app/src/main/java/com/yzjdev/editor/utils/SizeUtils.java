package com.yzjdev.editor.utils;
import android.content.Context;

public class SizeUtils {
    
	public static int px2dp(Context context,float px){
		float scale=context.getResources().getDisplayMetrics().density;
		return (int)(px/scale+0.5f);
		
	}
    public static int dp2px(Context context,float dp){
		float scale=context.getResources().getDisplayMetrics().density;
		return (int)(dp*scale+0.5f);
	}
    
}
