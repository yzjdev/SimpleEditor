package com.yzjdev.editor.text;

import com.yzjdev.editor.widget.CodeEditor;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class UndoManager {
    CodeEditor editor;
	public UndoManager(CodeEditor editor){
		this.editor=editor;
	}
    
	List<UndoStack> undoStack=new ArrayList<>();
	List<RedoStack> redoStack=new ArrayList<>();
	
	public boolean canUndo(){
		return undoStack.size()!=0;
	}
    
	public boolean canRedo(){
		return redoStack.size()!=0;
	}
    
	public void undo(){
		if(canUndo()){
			UndoStack undo= undoStack.get(undoStack.size()-1);
			undoStack.remove(undoStack.size()-1);
			editor.delete(undo.pos,undo.text.length());
		}
	}
	
	public void redo(){
		if(canRedo()){
			int last=redoStack.size()-1;
			RedoStack redo=redoStack.get(last);
			redoStack.remove(last);
			editor.insert(redo.pos,redo.text);
		}
	}
	
	public void tracker(int pos,String text,ACTION action){
		if(action==ACTION.DEL){
			redoStack.add(new RedoStack(pos,text));
		}else if(action==ACTION.ADD){
			undoStack.add(new UndoStack(pos,text));
		}
	}
	
	class UndoStack extends CommonStack{
		public UndoStack(int pos,String text){
			action=ACTION.DEL;
			this.pos=pos;
			this.text=text;
		}
	}
	
	class RedoStack extends CommonStack{
		public RedoStack(int pos,String text){
			action=ACTION.ADD;
			this.pos=pos;
			this.text=text;
		}
	}
	
	public class CommonStack{
		ACTION action;
		int pos;
		String text;
	}
	
	public enum ACTION{
		DEL,
		ADD
	}
	
	public void reset(){
		undoStack.clear();
		redoStack.clear();
	}
}
