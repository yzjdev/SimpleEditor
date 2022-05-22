package com.yzjdev.editor.widget;
import android.text.Editable;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;

public class KeyMetaStates extends MetaKeyKeyListener{
    
    private final CodeEditor editor;

    /**
     * Dummy text used for Android original APIs
     */
    private final Editable dest = Editable.Factory.getInstance().newEditable("");

    public KeyMetaStates(CodeEditor editor) {
        this.editor = editor;
    }

    public void onKeyDown(KeyEvent event) {
        super.onKeyDown(editor, dest, event.getKeyCode(), event);
    }

    public void onKeyUp(KeyEvent event) {
        super.onKeyUp(editor, dest, event.getKeyCode(), event);
    }

    public boolean isShiftPressed() {
        return getMetaState(dest, META_SHIFT_ON) != 0;
    }

    public boolean isAltPressed() {
        return getMetaState(dest, META_ALT_ON) != 0;
    }

    public void adjust() {
        adjustMetaAfterKeypress(dest);
    }

    public void clearMetaStates(int states) {
        clearMetaKeyState(editor, dest, states);
    }
    
}
