package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;

public class Cursor {

    CodeEditor editor;
    public int pos,line,column;

    public Cursor(CodeEditor editor) {
        this.editor = editor;
        pos = line = column = 0;
    }

    public void set(int pos){
        this.pos=pos;
    }
    
    public void set(int line,int column){
        this.line=line;
        this.column=column;
    }
    
    public int getLeft() {
        int left=pos - 1;
        return left < 0 ?0: left;
    }

    public int getRight() {
        return pos;
    }
}
