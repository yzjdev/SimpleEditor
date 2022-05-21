package com.yzjdev.simpleeditor;
 
import android.app.Activity;
import android.os.Bundle;
import com.yzjdev.editor.widget.CodeEditor;
import com.yzjdev.editor.utils.FileUtils;

public class MainActivity extends Activity { 
     CodeEditor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editor=findViewById(R.id.editor);
        String str=FileUtils.readAssets(this,"View.java");
        editor.setText(str);
    }
	
} 
