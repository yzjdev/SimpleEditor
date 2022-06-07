package com.yzjdev.editor.widget;

import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.Toast;
import com.yzjdev.editor.text.Cursor;

class EditorInputConnection extends BaseInputConnection {
	
    CodeEditor editor;

    public EditorInputConnection(CodeEditor targetView) {
        super(targetView, true);
        editor = targetView;
    }

    private Cursor getCursor() {
        return editor.getCursor();
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
		getCursor().stopBlink();
        editor.insert(text);
		getCursor().startBlink(1000);
        return true;
    }


    @Override
    public boolean performContextMenuAction(int id) {
        switch (id) {
            case android.R.id.selectAll:
                //mEditor.selectAll();
				Toast.makeText(editor.getContext(),"全选",0).show();
                return true;
            case android.R.id.cut:
				Toast.makeText(editor.getContext(),"剪切",0).show();
                
                return true;
            case android.R.id.paste:
            case android.R.id.pasteAsPlainText:
				Toast.makeText(editor.getContext(),"粘贴",0).show();
                //mEditor.pasteText();
                return true;
            case android.R.id.copy:
				Toast.makeText(editor.getContext(),"复制",0).show();
                //editor.copyText();
                return true;
            case android.R.id.undo:
                editor.undo();
                return true;
            case android.R.id.redo:
                editor.redo();
                return true;
        }
        return false;
    }

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        return null;
    }

}
