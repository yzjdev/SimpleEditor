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
        return super.commitText(text, newCursorPosition);
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        return super.getSelectedText(flags);
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        return super.getExtractedText(request, flags);
    }
    
    
}
