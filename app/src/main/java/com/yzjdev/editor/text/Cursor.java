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

    public void set(int pos) {
        this.pos = pos;
		Selection sel=editor.getSelection();
		sel.start = sel.end = pos;
		scrollToVisible();
    }

	public void toLeft() {
		pos--;
	}

	public void toRight() {
		pos++;
	}
    public int getLeft() {
		return pos - 1;
		//  int left=pos - 1;
        //return left < 0 ?0: left;
    }

    public int getRight() {
        return pos;
    }


	Timer timer;
	TimerTask task;
	public void startBlink(long delay) {
		task = new TimerTask(){

			@Override
			public void run() {
				editor.showCursor(!editor.isShowCursor());
			}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(task, delay, 500);
	}

	public void stopBlink() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		editor.showCursor(true);
	}


	public void scrollToVisible() {
		try {
			float x=editor.getX(editor.getSelection().isBatchEdit() ?editor.getSelection().end: pos) - editor.getLineNumberWidth();
			float y=editor.getY(editor.getSelection().isBatchEdit() ?editor.getSelection().end: pos);
			int width=editor.getWidth();
			int height=editor.getHeight();
			int currX=editor.getCurrX();
			int currY=editor.getCurrY();
			float lineNumberWidth=editor.getLineNumberWidth();
			float lineHeight=editor.getLineHeight();
			int dx=0;
			int dy=0;	
			if (x - currX + lineNumberWidth + 200 > width) {
				dx = (int)(x - width - currX + lineNumberWidth + 200);
			} else if (x - currX < 0) {
				dx = (int)(x - currX);
			}
			if (y - currY + lineHeight > height) {
				dy = (int)(y - height - currY + lineHeight);
			} else if (y - currY < 0) {
				dy = (int)(y - currY);
			}	
			editor.getScroller().startScroll(currX, currY, dx,dy);
			editor.invalidate();
		} catch (Exception e) {}
	}
}
