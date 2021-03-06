package com.yzjdev.editor.text;
import java.util.stream.IntStream;
import com.yzjdev.editor.widget.CodeEditor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.BadLocationException;
import android.widget.Toast;
import android.text.TextUtils;
import org.eclipse.jface.text.IDocumentListener;
import com.yzjdev.editor.lang.java.JavaHighlighter;
import java.util.List;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.graphics.Color;
import com.yzjdev.editor.utils.ToastUtils;

public class Content implements CharSequence {

    Document doc;
    CodeEditor editor;
    public Content(CodeEditor editor) {
        this.editor = editor;
        doc = new Document();
    }

	public void addDocumentListener(IDocumentListener l){
		doc.addDocumentListener(l);
	}
    
    
    public void setText(CharSequence text) {
		doc.set(text.toString());   
    }
	
    
    
    
	public boolean replace(int pos,int length,String text){
		try {
			doc.replace(pos, length, text);
			return true;
		} catch (BadLocationException e) {}
		return false;
	}

    public int getLineStart(int line) {
        try {
            return doc.getLineOffset(line);
        } catch (BadLocationException e) {}
        return 0;
    }
    
    public int getLineEnd(int line){
        return getLineStart(line)+length(line);
    }

    public int length(int line) {
        try {
            String delimiter=doc.getLineDelimiter(line);
            return lengthWithDelimiter(line) - (delimiter == null ?0: delimiter.length());
        } catch (BadLocationException e) {}
        return 0;
    }
    public int lengthWithDelimiter(int line) {
        try {
            return doc.getLineLength(line);
        } catch (BadLocationException e) {}
        return 0;
    }


    public String getText(int line) {
        try {
            return doc.get(doc.getLineOffset(line), length(line));
        } catch (BadLocationException e) {}
        return null;
    }

    public int getLineCount() {
        return doc.getNumberOfLines();
    }
    
    public int getLine(int pos){
        try {
            return doc.getLineOfOffset(pos);
        } catch (BadLocationException e) {}
        return 0;
    }

    @Override
    public int length() {
        return doc.getLength();
    }

    @Override
    public char charAt(int pos) {
        try {
            return doc.getChar(pos);
        } catch (BadLocationException e) {}
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return subString(start, end);
    }

    public String subString(int start, int end) {
        try {
            return doc.get(start, end - start);
        } catch (BadLocationException e) {}
        return null;
    }

    @Override
    public String toString() {
        return doc.get();
    }


    @Override
    public IntStream chars() {
        return null;
    }

    @Override
    public IntStream codePoints() {
        return null;
    }
}
