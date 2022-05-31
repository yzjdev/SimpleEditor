package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;

public class Selection {
    CodeEditor editor;
	public int start=0;
	public int end=0;

    private int nestedBatchEdit;
	public Selection(CodeEditor editor) {
		this.editor = editor;
	}

	public void selectLeft() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit() ?end: cursor.pos;
		if (pos <= 0)
			return;
		pos--;

		if (!isBatchEdit()) {
			start = end = cursor.pos =start!=end?end: pos;
		} else {
			end = pos;
		}
		editor.getCursor().scrollToVisible();
	}

	public void selectRight() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit() ?end: cursor.pos;
		if (pos >= editor.length())
			return;

		pos++;
		if (!isBatchEdit()) {
			start = end = cursor.pos =start!=end?end: pos;
		} else {
			end = pos;
		}
		editor.getCursor().scrollToVisible();
	}

	public void selectUp() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit() ?end: cursor.pos;
		if (pos <= 0)
			return;
		Content content=(Content) editor.getText();
		int line=content.getLine(pos);

		int offset=pos - content.getLineStart(line);
		if (line == 0)
			pos = 0;
		else {
			int nextLine=line - 1;
			pos = content.getLineStart(nextLine) + Math.min(offset, content.length(nextLine));
		}
		if (!isBatchEdit()) {
			start = end = cursor.pos = start!=end?end:pos;
		} else {
			end = pos;
		}
		editor.getCursor().scrollToVisible();
	}

	public void selectDown() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit() ?end: cursor.pos;
		if (pos >= editor.length())
			return;
		Content content=(Content) editor.getText();
		int line=content.getLine(pos);

		int offset=pos - content.getLineStart(line);
		if (line == editor.getLineCount() - 1)
			pos = editor.length();
		else {
			int nextLine=line + 1;
			pos = content.getLineStart(nextLine) + Math.min(offset, content.length(nextLine));
		}
		if (!isBatchEdit()) {
			start = end = cursor.pos =start!=end?end: pos;
		} else {
			end = pos;
		}
		editor.getCursor().scrollToVisible();
	}

	public boolean beginBatchEdit() {
		nestedBatchEdit++;
        return isBatchEdit();
	}

	public boolean endBatchEdit() {
		editor.getCursor().pos=start=end;
		nestedBatchEdit--;
        if (nestedBatchEdit < 0) {
            nestedBatchEdit = 0;
        }
        return isBatchEdit();
	}

	public boolean isBatchEdit() {
		return nestedBatchEdit > 0;
	}

	public int getSelectionStart() {
		return Math.min(start, end);
	}


	public int getSelectionEnd() {
		return Math.max(start, end);
	}

	public int length(){
		return Math.abs(end-start);
	}
	
	public void setSelection(int start,int end){
		this.start=start;
		this.end=end;
		
	}
	
}
