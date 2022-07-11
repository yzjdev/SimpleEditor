package com.yzjdev.editor.text;

public class Span {
    public int type;
    public int line;
    public int startIndex,endIndex;
	public int charPositionInLine;

    public Span(int type, int line, int startIndex, int endIndex, int charPositionInLine) {
        this.type = type;
        this.line = line;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.charPositionInLine = charPositionInLine;
    }



	@Override
	public String toString() {
		return String.format("startIndex=%s, endIndex=%s, charPosInLine=%s",startIndex,endIndex,charPositionInLine);
	}
    
	
}
