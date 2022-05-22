package com.yzjdev.editor.scheme;
import java.util.HashMap;
import android.util.SparseIntArray;
import android.graphics.Color;

public abstract class ColorScheme {

    public static final int COLOR_LINE_CURRENT=1;

    SparseIntArray colors;
    public ColorScheme() {
        colors = new SparseIntArray<>();
        apply();
    }

    public abstract void apply();

    public void setColor(int key, int color) {
        colors.put(key, color);
    }
    public void setColor(int key, String color) {
        colors.put(key, Color.parseColor(color));
    }

    public int getColor(int key) {
        try {
            return colors.get(key);
        } catch (Exception e) {}
        return Color.BLACK;
    }
}
