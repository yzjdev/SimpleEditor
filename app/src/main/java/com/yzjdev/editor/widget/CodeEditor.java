package com.yzjdev.editor.widget;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.graphics.Canvas;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import com.yzjdev.editor.text.Content;

public class CodeEditor extends View implements GestureDetector.OnGestureListener,ScaleGestureDetector.OnScaleGestureListener {

    

    public static final float DEFAULT_TEXT_SIZE=20f;
    float tabWidth,spaceWidth,lineHeight;
    
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    InputMethodManager imm;
    
    Content content;
    Paint paint;
    Paint.FontMetrics fontMetrics;
    Canvas canvas;
    Context context;
    
    public CodeEditor(Context context){
        this(context,null);
    }
    
    public CodeEditor(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context=context;
        
        scaleGestureDetector=new ScaleGestureDetector(context,this);
        gestureDetector=new GestureDetector(context,this);
        imm=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
       
        content=new Content(this);
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,DEFAULT_TEXT_SIZE,context.getResources().getDisplayMetrics()));
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/jetbrains_mono.ttf"));
        fontMetrics=paint.getFontMetrics();
        
        spaceWidth=paint.measureText(" ");
        tabWidth=spaceWidth*4;
        lineHeight=fontMetrics.descent-fontMetrics.ascent;
        
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas=canvas;
    }

    public void setText(CharSequence text){
        content.setText(text);
    }
    
    public int getLineCount(){
        return content.getLineCount();
    }
    
    public boolean isEditable(){
        return onCheckIsTextEditor() && isEnabled();
    }

    public void showSoftInput(){
        if(!hasFocus())
            requestFocus();
        imm.showSoftInput(this,0);
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
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        return false;
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
