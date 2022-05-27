package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;
import java.util.Timer;
import java.util.TimerTask;

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
		timer.cancel();
		timer.purge();
		timer=null;
		editor.showCursor(true);
	}
}
