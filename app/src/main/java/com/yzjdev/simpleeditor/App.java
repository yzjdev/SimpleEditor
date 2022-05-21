package com.yzjdev.simpleeditor;
import android.app.Application;
import com.yzjdev.crash.CrashHandler;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.init(this);
    }
    
    
    
}
