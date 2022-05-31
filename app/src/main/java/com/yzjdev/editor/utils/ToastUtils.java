package com.yzjdev.editor.utils;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    
    public static void showToast(Context context,Object str,int duration){
		Toast.makeText(context,String.valueOf(str),duration).show();
	}
    
	
	public static void shortToast(Context context,Object str){
		showToast(context,str,0);
	}
    public static void longToast(Context context,Object str){
		showToast(context,str,1);
	}
    
}
