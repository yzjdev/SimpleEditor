package com.yzjdev.editor.widget;
import android.view.inputmethod.BaseInputConnection;

public class EditableInputConnection extends BaseInputConnection{
    
    CodeEditor editor;
    public EditableInputConnection(CodeEditor editor){
        super(editor,true);
        this.editor=editor;
    }
    
}
