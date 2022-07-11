package com.yzjdev.crash;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.util.Log;

//<activity android:name="com.yzjdev.crash.CrashActivity"/>
public class CrashActivity extends Activity implements MenuItem.OnMenuItemClickListener {

	private static final String EXTRA_CRASH_INFO = "crashInfo";

	private String mLog;
	LinearLayout.LayoutParams lp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_DeviceDefault);
		getActionBar().setTitle("出错了！");
		mLog = getIntent().getStringExtra(EXTRA_CRASH_INFO);
		LinearLayout contentView=new LinearLayout(this);
		contentView.setOrientation(LinearLayout.VERTICAL);

		ScrollView container=new ScrollView(this);
		container.setFillViewport(true);
		HorizontalScrollView hsv=new HorizontalScrollView(this);
		hsv.setFillViewport(true);
		hsv.setHorizontalScrollBarEnabled(false);

		TextView tv=new TextView(this);
		tv.setTextColor(Color.WHITE);
		tv.setText(mLog);
		tv.setTextIsSelectable(true);
		int p=dp2px(24);
		tv.setPadding(p, p, p, p);

		hsv.addView(tv);

		container.addView(hsv);

		LinearLayout footer=new LinearLayout(this);
		Button closeBtn=new Button(this);
		closeBtn.setText("关闭");
		closeBtn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					finish();
				}
			});
		Button restartBtn=new Button(this);
		restartBtn.setText("重启");
		restartBtn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					restart();
				}
			});
		footer.addView(closeBtn);
		footer.addView(restartBtn);

		contentView.addView(container);
		contentView.addView(footer);

		setContentView(contentView);
		lp = new LinearLayout.LayoutParams(-1, -1, 1);
		container.setLayoutParams(lp);

		lp = new LinearLayout.LayoutParams(-1, -2, 1);
		int m=dp2px(12);
		lp.setMargins(m, m, m, m);
		closeBtn.setLayoutParams(lp);
		restartBtn.setLayoutParams(lp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, android.R.id.copy, 0, android.R.string.copy).setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.copy: 
				ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setPrimaryClip(ClipData.newPlainText(getPackageName(), mLog));
				Toast.makeText(this, "已写入剪切板", 0).show();
				break;
		}
		return false;
	}

	public static void actionStart(Context context, String errorMsg) {
		Intent intent = new Intent(context, CrashActivity.class);
		int flags=Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK;
		intent.addFlags(flags);
		intent.putExtra(CrashActivity.EXTRA_CRASH_INFO, errorMsg);
		context.startActivity(intent);
	}

	private int dp2px(final float dpValue) {
		final float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void restart() {
		Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
}
