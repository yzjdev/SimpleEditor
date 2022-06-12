package com.yzjdev.simpleeditor;
 
import android.app.Activity;
import android.os.Bundle;
import com.yzjdev.editor.widget.CodeEditor;
import com.yzjdev.editor.utils.FileUtils;
import android.view.Menu;
import android.view.MenuItem;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main,menu);
		menu.findItem(R.id.fixedLineNumber).setChecked(editor.isFixedLineNumber());
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.fixedLineNumber:
				item.setChecked(!editor.isFixedLineNumber());
				editor.setFixedLineNumber(item.isChecked());
				break;
			case R.id.readView:
				editor.setText(FileUtils.readAssets(this,"View.java"));
				break;
			case R.id.undo:
				editor.undo();
				break;
			case R.id.redo:
				editor.redo();
				break;
			case R.id.clearText:
				editor.setText("");
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
} 
