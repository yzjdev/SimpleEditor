package com.yzjdev.editor.text;
import com.yzjdev.editor.widget.CodeEditor;

public class Selection {
    
    CodeEditor editor;
	int start=0;
	int end=0;
	boolean isBatchEdit;
	public Selection(CodeEditor editor){
		this.editor=editor;
	}
	
	

	public void selectLeft(){
		Cursor cursor=editor.getCursor();
		int pos=cursor.pos;
		if(pos<=0)
			return;
		if(!isBatchEdit){
			pos--;
			cursor.pos=pos;
		}else{
			
		}
	}
	
	public void selectRight(){
		Cursor cursor=editor.getCursor();
		int pos=cursor.pos;
		if(pos>=editor.length())
			return;
		if(!isBatchEdit){
			pos++;
			cursor.pos=pos;
		}else{

		}
	}
	
	public void selectUp(){
		Cursor cursor=editor.getCursor();
		int pos=cursor.pos;
		if(pos<=0)
			return;
		Content content=(Content) editor.getText();
		int line=content.getLine(pos);
		
		int offset=pos-content.getLineStart(line);
		if(!isBatchEdit){
			if(line==0)
				pos=0;
			else{
				int nextLine=line-1;
				pos=content.getLineStart(nextLine)+Math.min(offset,content.length(nextLine));
			}
			cursor.pos=pos;
		}else{

		}
	}
	
	public void selectDown(){
		Cursor cursor=editor.getCursor();
		int pos=cursor.pos;
		if(pos>=editor.length())
			return;
		Content content=(Content) editor.getText();
		int line=content.getLine(pos);

		int offset=pos-content.getLineStart(line);
		if(!isBatchEdit){
			if(line==editor.getLineCount()-1)
				pos=editor.length();
			else{
				int nextLine=line+1;
				pos=content.getLineStart(nextLine)+Math.min(offset,content.length(nextLine));
			}
			cursor.pos=pos;
		}else{

		}
	}
	
	public void beginBatchEdit(){
		isBatchEdit=true;
	}
	
	public void endBatchEdit(){
		isBatchEdit=false;
	}
	
	public boolean isBatchEdit(){
		return isBatchEdit;
	}
}
