package com.min.mylibrary.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Min on 2016/8/10.
 * MD5加密
 */
public class MD5Util {
    public static String encryptMD5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md5=MessageDigest.getInstance("MD5");
        md5.update(data);//data是要加密的信息，格式为byte数组
        byte[] resultBytes=md5.digest();//即是经过 MD5 加密过后生成的 byte 数组
        String resultString=MD5Helper.bytesToHexString(resultBytes);
        return resultString;
    }
    public static String encode(String string) {
        MessageDigest md5= null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] byteArray=md5.digest();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<byteArray.length;i++){
            if(Integer.toHexString(0xFF & byteArray[i]).length()==1){
                stringBuffer.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            }else {
                stringBuffer.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return stringBuffer.substring(8,24).toString().toUpperCase();
    }
}
