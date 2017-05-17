package com.ideal.dzqd.flume.sink.httpclient;

import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by yaloo on 2015/11/27.
 */
public class Base64 {
    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {

            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        return new String(fromBase64(s));
    }

    public static byte[] fromBase64(String s){
        byte[] b = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            b = decoder.decodeBuffer(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}
