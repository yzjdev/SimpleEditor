package com.yzjdev.editor.utils;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IOUtils {
    
    public static String read(InputStream in)throws Exception{
        StringBuilder sb=new StringBuilder();
        char[] cbuf=new char[1024*8];
        int len;
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        while((len=reader.read(cbuf))!=-1){
            sb.append(cbuf,0,len);
        }
        reader.close();
        return sb.toString();
    }
    
}
