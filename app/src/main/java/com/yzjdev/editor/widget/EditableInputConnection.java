package com.yzjdev.editor.widget;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.ExtractedText;

public class EditableInputConnection extends BaseInputConnection{
    
    CodeEditor editor;
    public EditableInputConnection(CodeEditor editor){
        super(editor,true);
        this.editor=editor;
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
		editor.insert(text);
        return true;
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        return super.getSelectedText(flags);
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        return super.getExtractedText(request, flags);
    }

    @Override
    public CharSequence getTextBeforeCursor(int length, int flags) {
        return "a";
    }

    @Override
    public CharSequence getTextAfterCursor(int length, int flags) {
        return "a";
    }
    
    
    
}
