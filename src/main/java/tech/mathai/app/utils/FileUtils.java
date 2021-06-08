package tech.mathai.app.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.*;

/**
 * Created by mathai on 21-4-8.
 */
public class FileUtils {
    public static void write(String path,String bs) throws IOException {
        String dir=path.substring(0, path.lastIndexOf(File.separator));
        File filedir = new File(dir);
        filedir.mkdirs();

        OutputStream fs=new FileOutputStream(path);
        fs.write(bs.getBytes());
        fs.close();
    }
    public static void write(String path,JSONObject p) throws IOException {
        write(path,p.toString(SerializerFeature.PrettyFormat));
    }
    public static void makep(String p){

        File filedir = new File(p);
        filedir.mkdirs();
    }
    public static void main(String[] args) throws IOException {
        write("/home/mathai/zxk/123/a.txt","ss搜索a");
    }

    public static JSONObject readJson(String s) throws IOException {

        File f=new File(s);
        if(!f.exists()) return null;

        InputStream ins=new FileInputStream(s);
        byte[] buf=new byte[ins.available()];
        ins.read(buf);
        ins.close();

        return JSON.parseObject(new String(buf));
    }
}
