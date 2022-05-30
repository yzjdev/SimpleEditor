package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toast;

public class Cursor {

    CodeEditor editor;
    public int pos,line,column;

    public Cursor(CodeEditor editor) {
        this.editor = editor;
        pos = line = column = 0;
    }

    public void set(int pos){
        this.pos=pos;
    }
    
    public void set(int line,int column){
        this.line=line;
        this.column=column;
    }
	
	public void toLeft(){
		pos--;
	}
    
	public void toRight(){
		pos++;
	}
    public int getLeft() {
        int left=pos - 1;
        return left < 0 ?0: left;
    }

    public int getRight() {
        return pos;
    }
	
	
	Timer timer;
	TimerTask task;
	public void startBlink(long delay){
		task = new TimerTask(){

			@Override
			public void run() {
				editor.showCursor(!editor.isShowCursor());
			}
		};
		timer=new Timer();
		timer.scheduleAtFixedRate(task,delay,500);
	}
	
	public void stopBlink(){
		if(timer!=null){
			timer.cancel();
			timer.purge();
			timer=null;
		}
		editor.showCursor(true);
	}
	
	
	public void scrollToVisible(){
		float x=editor.getX(pos)-editor.getLineNumberWidth();
		float y=editor.getY(pos);
		int width=editor.getWidth();
		int height=editor.getHeight();
		int currX=editor.getCurrX();
		int currY=editor.getCurrY();
		
		int dx=0;
		int dy=0;


			
		if(x-currX+editor.getLineNumberWidth()+200>width){
			dx=(int)(x-width-currX+editor.getLineNumberWidth()+200);
		}else if(x-currX<0){
			dx=(int)(x-currX);
		}


		if(y-currY+editor.getLineHeight()>height){
			dy=(int)(y-height-currY+editor.getLineHeight());
		}else if(y-currY<0){
			dy=(int)(y-currY);
		}
			
		editor.getScroller().startScroll(currX,currY,dx,dy);
		editor.invalidate();
		//Toast.makeText(editor.getContext(),(x-currX>width)+"",0).show();
	}
}
