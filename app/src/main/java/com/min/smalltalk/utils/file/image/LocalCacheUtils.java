package com.min.smalltalk.utils.file.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Min on 2016/8/12.
 * 三级缓存之本地缓存
 */
public class LocalCacheUtils {
    private static final String CACHE_PATH=
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmallTalk/";

    /**
     * 从本地读取图片
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url){
        String fileName = null;//把图片的url当做文件名,并进行MD5加密
        try {
            File file = new File(CACHE_PATH,url);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url,Bitmap bitmap){
        try {
//            String fileName = MD5Util.encode(url);//把图片的url当做文件名,并进行MD5加密
            File file=new File(CACHE_PATH,url);
            Log.e("-=-==-=-=-=-=",CACHE_PATH);

            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()){
                parentFile.mkdirs();
            }
            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
