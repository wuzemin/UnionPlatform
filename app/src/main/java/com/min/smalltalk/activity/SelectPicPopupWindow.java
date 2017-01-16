package com.min.smalltalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.min.mylibrary.util.CommonUtils;
import com.min.smalltalk.R;

import java.io.File;
import java.util.List;

import static com.min.mylibrary.util.PhotoUtils.CROP_FILE_NAME;
import static com.min.mylibrary.util.PhotoUtils.INTENT_SELECT;
import static com.min.mylibrary.util.PhotoUtils.INTENT_TAKE;

/**
 * Created by Min on 2016/12/19.
 *  选择图片
 */

public class SelectPicPopupWindow  extends Activity implements View.OnClickListener {

    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bottom);
        intent = getIntent();
        btn_take_photo = (Button) this.findViewById(R.id.btn_photograph);
        btn_pick_photo = (Button) this.findViewById(R.id.btn_photo);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        /*layout = (LinearLayout) findViewById(R.id.pop_layout);

        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", Toast.LENGTH_SHORT).show();
            }
        });*/
        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode != RESULT_OK) {
            return;
        }
        //选择完或者拍完照后会在这里处理，然后我们继续使用setResult返回Intent以便可以传递数据和调用
        if (data.getExtras() != null)
            intent.putExtras(data.getExtras());
        if (data.getData()!= null)
            intent.setData(data.getData());
        setResult(1, intent);
        finish();*/

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photograph:
//            selectPicture();


          /*  case R.id.btn_photograph:
                try {
                    //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_photo:
                try {
                    //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {

                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;*/
        }

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

    protected boolean isIntentAvailable(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 构建uri
     */
    private Uri buildUri(Activity activity){
        if(CommonUtils.checkSDCard()){
            return Uri.fromFile(
                    Environment.getExternalStorageDirectory()).buildUpon().appendPath(CROP_FILE_NAME).build();
        }else{
            return Uri.fromFile(activity.getCacheDir()).buildUpon().appendPath(CROP_FILE_NAME).build();
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
                Log.i("----------", "Cached crop file cleared.");
            } else {
                Log.e("----------", "Failed to clear cached crop file.");
            }
            return result;
        }else {
            Log.w("----------", "Trying to clear cached crop file but it does not exist.");
        }
        return false;

    }
}
