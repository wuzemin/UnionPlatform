package com.min.mylibrary.util;

/**
 * Created by Min on 2016/8/10.
 * 平常见到的MD5格式都是16进制的
 * 所以我们需要将byte形式转换为十六进制
 */
public class MD5Helper {
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder=new StringBuilder("");
        if(src==null || src.length<=0){
            return null;
        }
        for(int i=0;i<src.length;i++){
            int v=src[i] & 0xFF;
            String hv= Integer.toHexString(v);
            if(hv.length()<2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
