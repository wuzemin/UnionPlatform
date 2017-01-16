package com.min.mylibrary.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Min on 2016/11/29.
 * 从本地选择图片以及拍照工具类
 */

public class PhotoUtils {

    private final String tag=PhotoUtils.class.getSimpleName();

    //裁剪图片成功后返回
    public static final int INTENT_CROP=2;
    //拍照
    public static final int INTENT_TAKE=3;
    //相册
    public static final int INTENT_SELECT=4;

    private SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyyMMddHHmm");
    private Date curDate = new Date(System.currentTimeMillis());//获取当前时间
    private String str = simpleDateFormat.format(curDate);
    public static final String CROP_FILE_NAME="str"+".jpg";

    /**
     * PhotoUtils 对象
     */
    public OnPhotoResultListener onPhotoResultListener;

    public PhotoUtils(OnPhotoResultListener onPhotoResultListener) {
        this.onPhotoResultListener = onPhotoResultListener;
    }

    /**
     * 拍照
     */
    public void takePicture(Activity activity){
        try {
            //每次选择图片把之前的图片删除
            clearCropFile(buildUri(activity));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, buildUri(activity));
            if (!isIntentAvailable(activity, intent)) {
                return;
            }
            activity.startActivityForResult(intent, INTENT_TAKE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 选择图片
     * @param activity
     * @return
     */
    @SuppressLint("InlinedApi")
    public void selectPicture(Activity activity) {
        try {
            //每次选择图片吧之前的图片删除
            clearCropFile(buildUri(activity));
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            if (!isIntentAvailable(activity, intent)) {
                return;
            }
            activity.startActivityForResult(intent, INTENT_SELECT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回结果处理
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
        if(onPhotoResultListener==null){
            L.e(tag,"onPhotoResultListener is not null");
            return;
        }
        switch (requestCode){
            //拍照
            case INTENT_TAKE:
                if(new File(buildUri(activity).getPath()).exists()){
                    if(crop(activity,buildUri(activity))){
                        return;
                    }
                    onPhotoResultListener.onPhotoCancel();
                }
                break;
            case INTENT_SELECT:  //选择图片
                if(data!=null && data.getData()!=null){
                    Uri imageUri=data.getData();
                    if(crop(activity,imageUri)){
                        return;
                    }
                }
                onPhotoResultListener.onPhotoCancel();
                break;
            case INTENT_CROP:  //截图
                if(resultCode==Activity.RESULT_OK && new File(buildUri(activity).getPath()).exists()){
                    onPhotoResultListener.onPhotoResult(buildUri(activity));
                }
                break;
        }
    }
    private boolean crop(Activity activity,Uri uri){
        Intent cropIntent=new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri,"image/*");
        cropIntent.putExtra("crop","true");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("outputX",200);
        cropIntent.putExtra("outputY",200);
        cropIntent.putExtra("return-data",false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        Uri cropUri=buildUri(activity);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,cropUri);
        if(!isIntentAvailable(activity,cropIntent)){
            return false;
        }else {
            try {
                activity.startActivityForResult(cropIntent, INTENT_CROP);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    protected boolean isIntentAvailable(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 构建uri
     */
    private Uri buildUri(Activity activity){
        String str = simpleDateFormat.format(curDate);
        if(CommonUtils.checkSDCard()){
            File file =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmallTalk/");
            return Uri.fromFile(file).buildUpon().appendPath(str+".jpg").build();
//                    Environment.getExternalStorageDirectory()).buildUpon().appendPath(CROP_FILE_NAME).build();
        }else{
            return Uri.fromFile(activity.getCacheDir()).buildUpon().appendPath(str+".jpg").build();
        }
    }

    /**
     * 删除文件
     */
    public boolean clearCropFile(Uri uri){
        if(uri==null){
            return false;
        }
        File file=new File(uri.getPath());
        if(file.exists()){
            boolean result=file.delete();
            if (result) {
                Log.i(tag, "Cached crop file cleared.");
            } else {
                Log.e(tag, "Failed to clear cached crop file.");
            }
            return result;
        }else {
            Log.w(tag, "Trying to clear cached crop file but it does not exist.");
        }
        return false;

    }

    /**
     * 回调监听类
     */
    public interface OnPhotoResultListener{
        void onPhotoResult(Uri uri);
        void onPhotoCancel();
    }

    public OnPhotoResultListener getOnPhotoResultListener() {
        return onPhotoResultListener;
    }

    public void setOnPhotoResultListener(OnPhotoResultListener onPhotoResultListener) {
        this.onPhotoResultListener = onPhotoResultListener;
    }
}
