package com.yzjdev.editor.utils;
import java.io.FileInputStream;
import android.content.Context;

public class FileUtils {

    public static String read(String path) {
        try {
            return IOUtils.read(new FileInputStream(path));
        } catch (Exception e) {}
        return null;
    }

    public static String readAssets(Context context, String fileName) {
        try {
            return   IOUtils.read(context.getAssets().open(fileName));
        } catch (Exception e) {}
        return null;
    }
}
