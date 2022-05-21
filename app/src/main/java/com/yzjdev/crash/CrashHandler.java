package com.yzjdev.crash;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CrashHandler implements Thread.UncaughtExceptionHandler {

	private Application mApp;
    private UncaughtExceptionHandler mDefaultHandler;

    public static void init(Application app) {
		new CrashHandler(app);
	}

	private CrashHandler(Application app) {
		mApp = app;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable ex) {
		try {
			handleException(ex);
		} catch (Exception e) {
			if (mDefaultHandler != null)
				mDefaultHandler.uncaughtException(t, ex);
		}
	}

	private void handleException(Throwable ex) {
		StringBuilder sb=new StringBuilder();

		String time = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
		String versionName = "unknown";
		long versionCode = 0;
		try { 
			PackageInfo packageInfo = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0);
			versionName = packageInfo.versionName;
			versionCode = Build.VERSION.SDK_INT >= 28 ? packageInfo.getLongVersionCode(): packageInfo.versionCode;
		} catch (Exception e) {}
		sb.append("************* Crash Head ****************\n");
		sb.append("Time Of Crash      : ").append(time).append("\n");
		sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
		sb.append("Device Model       : ").append(Build.MODEL).append("\n");
		sb.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n");
		sb.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n");
		sb.append("App VersionName    : ").append(versionName).append("\n");
		sb.append("App VersionCode    : ").append(versionCode).append("\n");
		sb.append("CPU_ABI            : " + Build.CPU_ABI).append("\n");
		sb.append("************* Crash Head ****************\n\n");

		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		ex.printStackTrace(pw);
		sb.append(sw.toString());
		pw.close();

		CrashActivity.actionStart(mApp, sb.toString().trim());
		System.exit(0);
	}


}

