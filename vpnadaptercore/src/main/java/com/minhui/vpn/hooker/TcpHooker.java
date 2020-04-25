package com.minhui.vpn.hooker;

import android.util.Log;

import com.minhui.vpn.nat.NatSession;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import okio.BufferedSource;
import okio.Okio;

public class TcpHooker {
    static LinkedHashMap<String, ResponseMock> map;
    NatSession session;
    static {
       map = new LinkedHashMap<>();
        ResponseMock heartMock = new ResponseMock("http://106.53.15.199/index.php/api/SingleCard/heart",
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Connection: keep-alive\n" +
                "\r\n",
                "{\"code\":\"1807\",\"data\":\"\"}");
        ResponseMock loginMock = new ResponseMock("http://106.53.15.199/index.php/api/SingleCard/login",
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n",
                "{\"code\":\"1707\",\"data\":{\"endtime\":\"2000000000\",\"point\":\"1\",\"token\":\"S\"}}");
        map.put(heartMock.url,heartMock);
        map.put(loginMock.url,loginMock);
    }

    public TcpHooker(NatSession session) {
        this.session = session;
    }

    public ByteBuffer afterReceived(ByteBuffer buffer){
        try {
            String url = session.getRequestUrl();
            if (map.containsKey(url)){
                ResponseMock responseMock = map.get(url);
                System.out.println("tcp mock : \n" + url + "\n" + responseMock.header);
                long l = System.currentTimeMillis();
                byte[] bytes = (responseMock.header + responseMock.body).getBytes();
                long l1 = System.currentTimeMillis();
                Log.e("zxj", "afterReceived: " + (l1 - l) );
                buffer = ByteBuffer.wrap(bytes);
                Log.e("zxj", "wrap: " + (System.currentTimeMillis() - l1) );
            }
        }catch (Throwable throwable){
            System.out.println(throwable.getMessage());
        }

        return buffer;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static byte[] compress(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

}
