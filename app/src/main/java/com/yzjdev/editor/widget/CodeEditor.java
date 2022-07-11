package com.yzjdev.editor.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import com.yzjdev.editor.scheme.ColorScheme;
import com.yzjdev.editor.scheme.DefalutColorScheme;
import com.yzjdev.editor.text.Content;
import com.yzjdev.editor.text.Cursor;
import com.yzjdev.editor.text.Selection;
import io.github.rosemoe.sora.event.EditorKeyEvent;
import io.github.rosemoe.sora.event.EventManager;
import io.github.rosemoe.sora.event.InterceptTarget;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import com.yzjdev.editor.text.UndoManager;
import com.yzjdev.editor.utils.ToastUtils;

public class CodeEditor extends View implements GestureDetector.OnGestureListener,ScaleGestureDetector.OnScaleGestureListener {

    public static final float DEFAULT_TEXT_SIZE=20f;
    float tabWidth,spaceWidth,lineHeight;
    float maxLineWidth;
    float lineNumberOffset=40;
    boolean isFixedLineNumber;
	boolean showCursor=true;
    EditorInputConnection inputConnection;
    EventManager eventManager;
    KeyMetaStates keyMetaStates;
    Selection selection;
    Cursor cursor;
    ColorScheme colorScheme;
    Scroller scroller;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    InputMethodManager imm;

	//AutoCompletePannel autoCompletePannel;
	UndoManager undoManager;
    Content content;
    Paint paint;
    Paint.FontMetrics fontMetrics;
    Canvas canvas;
    Context context;

    public CodeEditor(Context context) {
        this(context, null);
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

		//autoCompletePannel=new AutoCompletePannel(this);
		undoManager=new UndoManager(this);
        inputConnection = new EditorInputConnection(this);
        eventManager = new EventManager();
        keyMetaStates = new KeyMetaStates(this);
        selection = new Selection(this);
        cursor = new Cursor(this);
        colorScheme = new DefalutColorScheme();
        scroller = new Scroller(context);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        gestureDetector = new GestureDetector(context, this);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        content = new Content(this);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, context.getResources().getDisplayMetrics()));
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/jetbrains_mono.ttf"));
        fontMetrics = paint.getFontMetrics();

        spaceWidth = paint.measureText(" ");
        tabWidth = spaceWidth * 4;
        lineHeight = fontMetrics.descent - fontMetrics.ascent;

        setFocusable(true);
        setFocusableInTouchMode(true);
		cursor.startBlink(0);
		
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        //可视行
        int startLine=(int)Math.max(0, getCurrY() / lineHeight);
        int endLine=(int)Math.min(getLineCount(), Math.ceil((getCurrY() + getHeight()) / lineHeight));
        float x=0;
        float y=0;

		if (!selection.isBatchEdit()) {
			//绘制当前行背景
			int line=content.getLine(cursor.pos);
			paint.setColor(colorScheme.getColor(ColorScheme.COLOR_LINE_CURRENT));
			canvas.drawRect(0, line * lineHeight, getScrollX() + getWidth(), line * lineHeight + lineHeight, paint);
		}

		if (showCursor && !selection.isBatchEdit()) {
			//绘制光标
			int pos=cursor.pos;
			int line=content.getLine(pos);
			String text=content.getText(line);
			float[] widths=getTextWidths(text);
			float w=getLineNumberWidth();
			for (int i=0;i < pos - content.getLineStart(line);i++) {
				if (text.charAt(i) == '\t')
					widths[i] = tabWidth;
				w += widths[i];
			}
			paint.setStrokeWidth(6);
			paint.setColor(Color.BLUE);
			canvas.drawLine(w, line * lineHeight, w, (line + 1) * lineHeight, paint);
		}


		//绘制选择状态时start end的光标
		int lineStart,lineEnd;
		int selectionStart=selection.getSelectionStart();
		int selectionEnd=selection.getSelectionEnd();
		if (selection.isBatchEdit()) {
			float startX=getX(selectionStart);
			float startY=getY(selectionStart);
			float endX=getX(selectionEnd);
			float endY=getY(selectionEnd);
			paint.setColor(Color.BLUE);
			paint.setStrokeWidth(6);
			canvas.drawLine(startX, startY, startX, startY + lineHeight, paint);
			canvas.drawLine(endX, endY, endX, endY + lineHeight, paint);
		}

		//开始绘制文本
        for (int i=startLine;i < endLine;i++) {
            x = getLineNumberWidth();
            //文本基线
            y = i * lineHeight - fontMetrics.ascent;

			//绘制选中区域背景
			//共有四种情况 1.[] 2.() 3.(] 4.[)
			RectF rectf=new RectF();
			rectf.top = i * lineHeight;
			rectf.bottom = rectf.top + lineHeight;
			if (selection.isBatchEdit()) {
				lineStart = content.getLineStart(i);
				lineEnd = content.getLineEnd(i);
				if (lineStart >= selectionStart && lineEnd <= selectionEnd) {
					//当前行在选中区域内[]
					rectf.left = x;
					if (content.length(i) == 0) {
						rectf.right = rectf.left + 6;
					} else {
						rectf.right = rectf.left + getTextWidth(content.getText(i));
					}
				}  else if (lineStart < selectionStart && lineEnd > selectionEnd) {
					//选择开始和结束在当前行内()
					float[] widths=getTextWidths(content.getText(i));
					float st=getLineNumberWidth();
					for (int l=0;l < selectionStart - lineStart;l++) {
						st += widths[l];
					}
					float en = getLineNumberWidth();
					for (int l=0;l < selectionEnd - lineStart;l++) {
						en += widths[l];
					}
					rectf.left = st;
					rectf.right = en;
				} else if (lineStart < selectionStart && lineEnd > selectionStart) {
					//选择开始在当前行，选择结束不在当前行 (]
					float[] widths=getTextWidths(content.getText(i));
					float st=getLineNumberWidth();
					for (int l=0;l < selectionStart - lineStart;l++) {
						st += widths[l];
					}
					rectf.left = st;
					rectf.right = x + getTextWidth(content.getText(i));
				} else if (lineStart < selectionEnd && lineEnd > selectionEnd) {
					//选择开始不在当前行，选择结束在当前行[)
					float[] widths=getTextWidths(content.getText(i));
					float en=getLineNumberWidth();
					for (int l=0;l < selectionEnd - lineStart;l++) {
						en += widths[l];
					}
					rectf.left = x;
					rectf.right = en;
				}
				paint.setColor(Color.LTGRAY);
				canvas.drawRoundRect(rectf, 8, 8, paint);
			}


            String text=content.getText(i);
            if (text.length() > 1000)//文本超长没有很好的解决方式，则不绘制，防止App崩溃
                continue;
            float[] widths = getTextWidths(text);
            //开始绘制文本，按char绘制
            paint.setTextAlign(Paint.Align.LEFT);
            int color;
            char[] cs=new char[2];
            int count=1;

            for (int j=0;j < widths.length;j += count) {
                color = Color.BLACK;
                char c=text.charAt(j);
                if (c == ' ') {
                    //处理空格
                    c = '·';
                    color = Color.LTGRAY;
                } else if (c == '\t') {
                    //处理tab
                    widths[j] = tabWidth;
                }

                cs[0] = c;
                count = 1;

                //处理表情，仅支持两个char的表情
                if (Character.isHighSurrogate(c) && (j + 1) < text.length()) {
                    c = text.charAt(j + 1);
                    if (Character.isLowSurrogate(c)) {
                        cs[1] = c;
                        count = 2;
                    }   
                }
                paint.setColor(color);
                canvas.drawText(cs, 0, count, x , y, paint);
                x += widths[j];
            }

            if (x > maxLineWidth)//行最大宽度
                maxLineWidth = x;

            //绘制行号背景
            float l=isFixedLineNumber ?getCurrX(): 0;
            float t=i * lineHeight;
            float r=l + getLineNumberWidth() - lineNumberOffset / 2;
            float b=t + lineHeight;
            paint.setColor(Color.WHITE);
            canvas.drawRect(l, t, r, b, paint);
            //绘制分割线
            float ax=r;
            float ay=t;
            float bx=ax;
            float by=b;
			paint.setStrokeWidth(0);
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(ax, ay, bx, by, paint);
            //绘制行号
            ax -= lineNumberOffset / 2;
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(i + 1), ax, y, paint);
        }


    }

	public void delete() {
		delete(cursor.pos - 1, 1);
	}
	public void delete(int pos, int length) {
		replace(pos, length, "");
	}
	public void insert(CharSequence text) {
		insert(cursor.pos, text);
	}
	public void insert(int pos, CharSequence text) {
		replace(pos, 0, text);
	}
	public void replace(int pos, int length, CharSequence text) {
		String str=content.subString(pos,pos+length);
		if (content.replace(pos, length, text.toString())) {
			int newpos = pos + text.length();
			boolean isDelAction=newpos==pos;
			if(isDelAction){
				//删除
				undoManager.tracker(pos,str,UndoManager.ACTION.DEL);
			}else{
				//插入
				undoManager.tracker(pos,text.toString(),UndoManager.ACTION.ADD);
				
			}
			
			cursor.pos=newpos;
			
			cursor.scrollToVisible();

			invalidate();

			//显示补全面板
			//if(isDelAction)autoCompletePannel.dismiss();
			//else autoCompletePannel.show();
		}
	}

	public float getLineHeight() {
		return lineHeight;
	}

    public void setText(CharSequence text) {
		
		content.setText(text);
		undoManager.reset();
		maxLineWidth=0;
		cursor.pos=0;
		//autoCompletePannel.dismiss();
    }

    public CharSequence getText() {
        return content;
    }

    public int getMaxX() {
		return (int)(getContentWidth() - getWidth());
    }

    public int getMaxY() {
        return (int)(getContentHeight() - getHeight());
    }

    public float getContentWidth() {
        return Math.max(getWidth(), maxLineWidth + 200);
    }

    public float getContentHeight() {
        return Math.max(getHeight(), getLineCount() * lineHeight);
    }

    public int getCurrX() {
        return scroller.getCurrX();
    }

    public int getCurrY() {
        return scroller.getCurrY();
    }
	
	public float getCursorX(){
		return getX(cursor.pos);
	}
	
	public float getCursorY(){
		return getY(cursor.pos);
	}

	//通过光标位置计算x
	public float getX(int pos) {
		try {

			int line=content.getLine(pos);
			String text=content.getText(line);
			float[] widths=getTextWidths(text);
			int lineStart=content.getLineStart(line);
			float x=getLineNumberWidth();
			for (int i=0;i < pos - lineStart;i++) {
				if (text.charAt(i) == '\t')
					widths[i] = tabWidth;
				x += widths[i];
			}
			return x;
		} catch (Exception e) {}
		return getLineNumberWidth();
	}


	//通过光标位置计算y
	public float getY(int pos) {
		try {
			return content.getLine(pos) * lineHeight;
		} catch (Exception e) {}
		return 0;
	}

	//获取文本长度数组
    private float[] getTextWidths(String text) {
        float[] widths=new float[text.length()];
        paint.getTextWidths(text, widths);
        return widths;
    }

    private float getTextWidth(String text) {
        float[] widths=getTextWidths(text);
        float width=0;
        for (int i=0;i < widths.length;i++) {
            if (text.charAt(i) == '\t')
                widths[i] = tabWidth;
            width += widths[i];
        }
        return width;
    }

    public float getLineNumberWidth() {
        return lineNumberOffset + paint.measureText(String.valueOf(getLineCount()));
    }

    public int getLineCount() {
        return content.getLineCount();
    }
	
	public void undo(){
		undoManager.undo();
	}
	
	public void redo(){
		undoManager.redo();
	}
	

    public boolean isEditable() {
        return onCheckIsTextEditor() && isEnabled();
    }

	public Scroller getScroller() {
		return scroller;
	}
	public Cursor getCursor() {
		return cursor;
	}

	public Selection getSelection() {
		return selection;
	}
	public int length() {
		return content.length();
	}
    public void showSoftInput() {
        if (!hasFocus())
            requestFocus();
        imm.showSoftInput(this, 0);
    }

    public void setFixedLineNumber(boolean z) {
        isFixedLineNumber = z;
		invalidate();
    }
	public boolean isFixedLineNumber() {
		return isFixedLineNumber;
	}
	public void showCursor(boolean z) {
		showCursor = z;
		invalidate();
	}

	public boolean isShowCursor() {
		return showCursor;
	}

    public KeyMetaStates getKeyMetaStates() {
        return keyMetaStates;
    }

	/*
	public AutoCompletePannel getAutoCompletePannel(){
		return autoCompletePannel;
	}*/

    /** 重写方法 手势 输入法 Scroller
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
		}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        keyMetaStates.onKeyDown(event);
        EditorKeyEvent e = new EditorKeyEvent(this, event);
        if ((eventManager.dispatchEvent(e) & InterceptTarget.TARGET_EDITOR) != 0) {
            return e.result(false);
        }
        boolean isShiftPressed = keyMetaStates.isShiftPressed();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_MOVE_END:
				if (isShiftPressed && !selection.isBatchEdit()) {
					selection.beginBatchEdit();
				} else if (!isShiftPressed) {
					selection.endBatchEdit();
				}
                keyMetaStates.adjust();
        }
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DEL:
			case KeyEvent.KEYCODE_ENTER:
				cursor.stopBlink();
				break;
		}
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                if (isEditable())
					delete();
                return e.result(true);
            case KeyEvent.KEYCODE_ENTER:
				if (isEditable()) 
					insert("\n");
				return e.result(true);
            case KeyEvent.KEYCODE_DPAD_DOWN:
				selection.selectDown();
                return e.result(true);
            case KeyEvent.KEYCODE_DPAD_UP:
				selection.selectUp();
                return e.result(true);
            case KeyEvent.KEYCODE_DPAD_LEFT:
				selection.selectLeft();
                return e.result(true);
            case KeyEvent.KEYCODE_DPAD_RIGHT:
				selection.selectRight();
                return e.result(true);
        }
        return e.result(super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        keyMetaStates.onKeyUp(event);
        EditorKeyEvent e = new EditorKeyEvent(this, event);
        if ((eventManager.dispatchEvent(e) & InterceptTarget.TARGET_EDITOR) != 0) {
            return e.result(false);
        }
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DEL:
			case KeyEvent.KEYCODE_ENTER:
				cursor.startBlink(1000);
				break;
		}
        return e.result(super.onKeyUp(keyCode, event));
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return inputConnection;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			cursor.scrollToVisible();
		}
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
		
		
		showSoftInput();
		//autoCompletePannel.dismiss();
		try {
			cursor.stopBlink();
			int line=(int)Math.min(getLineCount() - 1, ((e.getY() + getCurrY()) / lineHeight));
			String text=content.getText(line);
			float[] widths=getTextWidths(text);
			float x=getLineNumberWidth();
			int col=0;
			for (int i=0;i < widths.length;i++) {
				if (text.charAt(i) == '\t')
					widths[i] = tabWidth;
				x += widths[i];
				if (e.getX() + getCurrX() < x) {
					col = i;
					break;
				}
			}
			if (e.getX() + getCurrX() > x) {
				col = widths.length;
			}
			cursor.set(content.getLineStart(line) + col);

			cursor.startBlink(1000);
		} catch (Exception ex) {}
		return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int dx=(int)distanceX;
        int dy=(int)distanceY;
        if (getCurrX() + distanceX > getMaxX()) {
            dx = getMaxX() - getCurrX();
        } else if (getCurrX() + distanceX < 0) {
            dx = 0;
        }
        if (getCurrY() + distanceY > getMaxY()) {
            dy = getMaxY() - getCurrY();
        } else if (getCurrY() + distanceY < 0) {
            dy = 0;
        }
        scroller.forceFinished(true);
        scroller.startScroll(getCurrX(), getCurrY(), dx, dy, 0);
        invalidate();

		return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        scroller.forceFinished(true);

        scroller.fling(
            getScrollX(), getScrollY(),
            -(int)vx, -(int)vy, 
            0, getMaxX(), 
            0, getMaxY());

        invalidate();
		return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector d) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector d) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector d) {
    }


}
