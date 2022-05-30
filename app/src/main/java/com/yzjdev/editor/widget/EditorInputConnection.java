package com.yzjdev.editor.widget;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import com.yzjdev.editor.text.Content;
import com.yzjdev.editor.text.Cursor;
import com.yzjdev.editor.text.Selection;
import android.widget.Toast;

/**
 * Connection between input method and editor
 *
 * @author Rose
 */
class EditorInputConnection extends BaseInputConnection {

    private final static String LOG_TAG = "EditorInputConnection";

    private final CodeEditor mEditor;
    protected int mComposingLine = -1;
    protected int mComposingStart = -1;
    protected int mComposingEnd = -1;
    protected boolean mImeConsumingInput = false;
    private boolean mInvalid;

    /**
     * Create a connection for the given editor
     *
     * @param targetView Host editor
     */
    public EditorInputConnection(CodeEditor targetView) {
        super(targetView, true);
        mEditor = targetView;
        mInvalid = false;
    }

    protected void invalid() {
        mInvalid = true;
        mComposingEnd = mComposingStart = mComposingLine = -1;
        mEditor.invalidate();
    }

    /**
     * Reset the state of this connection
     */
    protected void reset() {
        mComposingEnd = mComposingStart = mComposingLine = -1;
        mInvalid = false;
        mImeConsumingInput = false;
    }

    /**
     * Private use.
     * Get the Cursor of Content displaying by Editor
     *
     * @return Cursor
     */
    private Cursor getCursor() {
        return mEditor.getCursor();
    }

    @Override
    public synchronized void closeConnection() {
        super.closeConnection();
		Selection selection=mEditor.selection;
        while (selection.isBatchEdit()) {
            selection.endBatchEdit();
        }
        mComposingLine = mComposingEnd = mComposingStart = -1;
        mEditor.onCloseConnection();
    }

    @Override
    public int getCursorCapsMode(int reqModes) {
        return TextUtils.getCapsMode(mEditor.getText(), getCursor().getLeft(), reqModes);
    }

    /**
     * Get content region internally
     */
    private CharSequence getTextRegionInternal(int start, int end, int flags) {
        Content origin = (Content) mEditor.getText();
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > origin.length()) {
            end = origin.length();
        }
        if (end < start) {
            start = end = 0;
        }
        if (end - start > 500000) {
            end = start + 500000;
        }
        
        return origin.subSequence(start, end);
    }

    protected CharSequence getTextRegion(int start, int end, int flags) {
        try {
            return getTextRegionInternal(start, end, flags);
        } catch (IndexOutOfBoundsException e) {
            Log.w(LOG_TAG, "Failed to get text region for IME", e);
            return "";
        }
    }

    @Override
    public CharSequence getSelectedText(int flags) {
        //This text should be limited because when the user try to select all text
        //it can be quite large text and costs time, which will finally cause ANR
        int left = getCursor().getLeft();
        int right = getCursor().getRight();
        return left == right ? null : getTextRegion(left, right, flags);
    }

    @Override
    public CharSequence getTextBeforeCursor(int length, int flags) {
        int start = getCursor().getLeft();
        return getTextRegion(start - length, start, flags);
    }

    @Override
    public CharSequence getTextAfterCursor(int length, int flags) {
        int end = getCursor().getRight();
        return getTextRegion(end, end + length, flags);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        mEditor.insert(text);
        return true;
    }

    @Override
    public synchronized boolean beginBatchEdit() {
        return mEditor.selection.beginBatchEdit();
    }

    @Override
    public synchronized boolean endBatchEdit() {
        boolean inBatch = mEditor.selection.endBatchEdit();
        if (!inBatch) {
            mEditor.updateSelection();
        }
        return inBatch;
    }


    private int getWrappedIndex(int index) {
        if (index < 0) {
            return 0;
        }
        if (index > mEditor.getText().length()) {
            return mEditor.getText().length();
        }
        return index;
    }

    @Override
    public boolean setSelection(int start, int end) {
        if (!mEditor.isEditable() || mInvalid || mComposingLine != -1) {
            return false;
        }
        start = getWrappedIndex(start);
        end = getWrappedIndex(end);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start == getCursor().getLeft() && end == getCursor().getRight()) {
            return true;
        }
      
		mEditor.selection.setSelection(start,end);
		return true;
    }

    @Override
    public boolean performContextMenuAction(int id) {
        switch (id) {
            case android.R.id.selectAll:
                //mEditor.selectAll();
				Toast.makeText(mEditor.getContext(),"全选",0).show();
                return true;
            case android.R.id.cut:
				Toast.makeText(mEditor.getContext(),"剪切",0).show();
                /*mEditor.copyText();
                if (getCursor().isSelected()) {
                    mEditor.deleteText();
                }
				*/
                return true;
            case android.R.id.paste:
            case android.R.id.pasteAsPlainText:
				Toast.makeText(mEditor.getContext(),"粘贴",0).show();
                //mEditor.pasteText();
                return true;
            case android.R.id.copy:
				Toast.makeText(mEditor.getContext(),"复制",0).show();
                //mEditor.copyText();
                return true;
            case android.R.id.undo:
                //mEditor.undo();
                return true;
            case android.R.id.redo:
                //mEditor.redo();
                return true;
        }
        return false;
    }

    

    @Override
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        if ((flags & GET_EXTRACTED_TEXT_MONITOR) != 0) {
            mEditor.setExtracting(request);
        } else {
            mEditor.setExtracting(null);
        }

        return mEditor.extractText(request);
    }

    @Override
    public boolean clearMetaKeyStates(int states) {
        mEditor.getKeyMetaStates().clearMetaStates(states);
        return true;
    }

    @Override
    public boolean reportFullscreenMode(boolean enabled) {
        return false;
    }

    @Override
    public Handler getHandler() {
        return mEditor.getHandler();
    }

}
