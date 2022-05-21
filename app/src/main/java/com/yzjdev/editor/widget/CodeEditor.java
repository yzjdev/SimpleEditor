package com.yzjdev.editor.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import com.yzjdev.editor.text.Content;

public class CodeEditor extends View implements GestureDetector.OnGestureListener,ScaleGestureDetector.OnScaleGestureListener {


    public static final float DEFAULT_TEXT_SIZE=20f;
    float tabWidth,spaceWidth,lineHeight;
    float maxLineWidth;
    float lineNumberOffset=40;
    boolean isFixedLineNumber=true;

    Scroller scroller;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    InputMethodManager imm;

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

        for (int i=startLine;i < endLine;i++) {
            x = getLineNumberWidth();
            //文本基线
            y = i * lineHeight - fontMetrics.ascent;
            String text=content.getText(i);
            if (text.length() > 1000)
                continue;
            float[] widths = new float[text.length()];
            paint.getTextWidths(text, widths);

            //开始绘制文本
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

            float l=isFixedLineNumber ?getCurrX(): 0;
            float t=i * lineHeight;
            float r=l + getLineNumberWidth() - lineNumberOffset / 2;
            float b=t + lineHeight;
            //绘制行号背景
            paint.setColor(Color.WHITE);
            canvas.drawRect(l, t, r, b, paint);
            //绘制分割线
           
            float ax=r;
            float ay=t;
            float bx=ax;
            float by=b;
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(ax,ay,bx,by, paint);
            //绘制行号
            paint.setTextAlign(Paint.Align.RIGHT);
            ax-=lineNumberOffset/2;
            canvas.drawText(String.valueOf(i + 1), ax, y, paint);
        }
    }


    public void setText(CharSequence text) {
        content.setText(text);
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
        return maxLineWidth;
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

    public boolean isEditable() {
        return onCheckIsTextEditor() && isEnabled();
    }

    public void showSoftInput() {
        if (!hasFocus())
            requestFocus();
        imm.showSoftInput(this, 0);
    }

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
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
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
        return false;
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
