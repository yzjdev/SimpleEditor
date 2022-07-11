package com.yzjdev.editor.lang.java;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import android.util.Log;
import java.util.List;
import com.yzjdev.editor.text.Span;

public class JavaHighlighter {
    JavaLexer lexer;
    public JavaHighlighter(){
		lexer=new JavaLexer(null);
	}
	
	public void tokenizer(String input,List<Span> spans){
		spans.clear();
		CharStream stream=CharStreams.fromString(input);
		lexer.setInputStream(stream);
		Token token;
		Span span;
		while(!lexer._hitEOF){
			token=lexer.nextToken();
			span=new Span(token.getType(),token.getLine(),token.getStartIndex(),token.getStopIndex(),token.getCharPositionInLine());
			spans.add(span);
		}
        
	}
    
    
    
}
