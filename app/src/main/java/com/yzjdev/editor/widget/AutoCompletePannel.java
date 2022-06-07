package com.yzjdev.editor.widget;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.yzjdev.editor.utils.SizeUtils;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.util.Pair;
import android.graphics.Point;

public class AutoCompletePannel implements AdapterView.OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
		editor.insert("test");
		dismiss();
	}
	
	CodeEditor editor;
	Context context;

	List<Data> datas=new ArrayList<>();
	PopupWindow pop;
	ListView lv;
	Adapter adapter;

	public AutoCompletePannel(CodeEditor editor) {
		this.editor = editor;
		context = editor.getContext();

		initData();
		//列表
		lv = new ListView(context);
		lv.setDividerHeight(0);
		adapter = new Adapter();
		adapter.setData(datas);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		//弹窗
		pop = new PopupWindow(context);
		pop.setContentView(lv);
		pop.setElevation(10);
		pop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bbffffff")));
		//设置弹窗宽度
		int popWidth=context.getResources().getDisplayMetrics().widthPixels - 400;
		pop.setWidth(popWidth);

		//获取列表item的高度
		View itemView= adapter.getView(0, null, lv);
		itemView.measure(0, 0);
		int itemHeight=itemView.getMeasuredHeight();

		//计算弹窗高度
		int itemCount=datas.size();
		int popHeight=itemHeight * itemCount;
		//最大显示4个item
		if (itemCount >= 4) {
			popHeight = itemHeight * 4;
		}
		pop.setHeight(popHeight);

	}

	void initData() {
		for (int i=0;i < 10;i++) {
			datas.add(new Data());
		}
	}

	public void dismiss() {
		if (pop != null)
			pop.dismiss();
	}
	
	Point computeLocation(){
		Point point=new Point();
		int[] offset=new int[2];
		//获取editor相对父控件坐标
		int[] outLocation=new int[2];
		editor.getLocationOnScreen(outLocation);

		float xoff=200;
		float yoff=outLocation[1];

		float cursorY=(int)editor.getCursorY() - editor.getCurrY();

		if (cursorY > editor.getHeight() / 2+editor.lineHeight) {
			//显示在当前行上方
			yoff += cursorY - pop.getHeight();
		} else {
			//显示在当前行下方
			yoff += cursorY + editor.lineHeight;	
		}
		point.x=(int)xoff;
		point.y=(int)yoff;
		return point;
	}
	public void update(){
		int xoff=computeLocation().x;
		int yoff=computeLocation().y;
		if (pop.isShowing()) {
			pop.update(xoff, yoff, -2, -2);
		}
	}

	public void show() {
		int xoff=computeLocation().x;
		int yoff=computeLocation().y;
		if (pop.isShowing()) {
			pop.update(xoff, yoff, -2, -2);
		} else {
			pop.showAtLocation(editor, Gravity.NO_GRAVITY, xoff, yoff);
		}

	}

	//适配器
	class Adapter extends BaseAdapter {
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int p1) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			AutoCompleteView view=new AutoCompleteView();
			view.setText("test");
			return view;
		}

		public void setData(List<Data> datas) {
			AutoCompletePannel.this.datas = datas;
		}


	}

	//数据类
	class Data {

	}

	//itemView
	class AutoCompleteView extends LinearLayout {
		TextView tv;
		public AutoCompleteView() {
			super(context);
			int dp8=SizeUtils.dp2px(context, 8);
			setPadding(dp8, dp8, dp8, dp8);
			
			tv = new TextView(context);
			tv.setTextColor(Color.BLACK);
			addView(tv, -1, -2);
		}

		public void setText(String text) {
			tv.setText(text);
		}
	}
}
