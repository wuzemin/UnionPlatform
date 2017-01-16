package com.min.smalltalk.utils.file;

import android.os.Environment;

import com.min.mylibrary.util.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Min on 2017/1/12.
 */

public class FileUtils {
    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }
    public FileUtils() {
        //得到当前外部存储设备的目录
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }
    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (!isFileExist(fileName)) {
            file.createNewFile();
        }

        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName){

        File file = new File(SDPATH + fileName);
        //file.delete();
        return file.exists();

    }
    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public File writeToSDFromInput(String path,String fileName,InputStream input){
        File file =null;
        OutputStream output =null;
        try{
            if (input==null) {
                return null;
            }
            creatSDDir(path);
//            String[] fileNames=fileName.split("/");
            /*for (int i = 0; i < fileNames.length-1; i++) {
                path+="/"+fileNames[i];
                creatSDDir(path);
            }*/
            byte buffer [] = new byte[1024];
            int len  = 0;
            //如果下载成功就开往SD卡里些数据
            while((len =input.read(buffer))  != -1){
                file = creatSDFile(path+"/"+ fileName);
                if (output==null) {
                    output = new FileOutputStream(file);
                }
                output.write(buffer,0,len);
            }
            output.flush();
            L.e("--------writeToSDFromInput", "保存图片到SD卡成功");
            input.close();
            output.close();
        }catch(Exception e){
            L.e("--------writeToSDFromInput", "保存图片到SD卡失败:"+e);
        }
        return file;
    }
}
