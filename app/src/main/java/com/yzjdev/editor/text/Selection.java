package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;

public class Selection {

    CodeEditor editor;
	int start=0;
	int end=0;
	boolean isBatchEdit;
	public Selection(CodeEditor editor) {
		this.editor = editor;
	}



	public void selectLeft() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit ?end: cursor.pos;
		if (pos <= 0)
			return;
		pos--;

		if (!isBatchEdit) {
			start = end = cursor.pos = pos;
		} else {
			end = pos;
		}
	}

	public void selectRight() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit ?end: cursor.pos;
		if (pos >= editor.length())
			return;

		pos++;
		if (!isBatchEdit) {
			start = end = cursor.pos = pos;
		} else {
			end = pos;
		}
	}

	public void selectUp() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit ?end: cursor.pos;
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
		if (!isBatchEdit) {

			start = end = cursor.pos = pos;
		} else {
			end = pos;
		}
	}

	public void selectDown() {
		Cursor cursor=editor.getCursor();
		int pos=isBatchEdit ?end: cursor.pos;
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
		if (!isBatchEdit) {

			start = end = cursor.pos = pos;
		} else {
			end = pos;
		}
	}

	public void beginBatchEdit() {
		isBatchEdit = true;
		editor.invalidate();
	}

	public void endBatchEdit() {
		isBatchEdit = false;
		editor.invalidate();
	}

	public boolean isBatchEdit() {
		return isBatchEdit;
	}

	public int getSelectionStart() {
		return Math.min(start, end);
	}


	public int getSelectionEnd() {
		return Math.max(start, end);
	}

}
