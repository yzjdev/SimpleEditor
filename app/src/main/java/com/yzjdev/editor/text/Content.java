package com.yzjdev.editor.text;
import java.util.stream.IntStream;
import com.yzjdev.editor.widget.CodeEditor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.BadLocationException;

public class Content implements CharSequence {

    Document doc;
    CodeEditor editor;
    public Content(CodeEditor editor){
        doc=new Document();
        this.editor=editor;
    }
    
    public void setText(CharSequence text){
        doc.set(text.toString());
    }
    
    public int getLineCount(){
        return doc.getNumberOfLines();
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
        try {
            return doc.get(start, end - start);
        } catch (BadLocationException e) {}
        return null;
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
