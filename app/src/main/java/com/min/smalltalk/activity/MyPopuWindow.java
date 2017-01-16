package com.min.smalltalk.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.base.BasePopup;
import com.min.smalltalk.utils.CameraUtils;

/**
 * Created by Min on 2016/12/19.
 *  相册--照片--popupwindow
 */

public class MyPopuWindow extends BasePopup {

    private View v;
    private Activity c;
    private Fragment f;

    public MyPopuWindow(Activity context){
        super(context);
        v.findViewById(R.id.popup_choose_from_camera).setOnClickListener(this);
        v.findViewById(R.id.popup_choose_from_album).setOnClickListener(this);

    }

    public MyPopuWindow(Activity activity, Context context, Fragment f) {
        super(activity);
        c=activity;
        this.f=f;
        v.findViewById(R.id.popup_choose_from_camera).setOnClickListener(this);
        v.findViewById(R.id.popup_choose_from_album).setOnClickListener(this);
    }

    @Override
    public void setTitleText() {
        ((TextView)v.findViewById(R.id.popup_base_title)).setText("请选择图片来源");
    }

    @Override
    public View getView() {
        v=inflateView(R.layout.popup_base);
        return v;
    }

    @Override
    public Animation setAnima() {
        return getScaleAnimation();
    }

    @Override
    public View getCancelButton() {
        return v.findViewById(R.id.popup_cancel);
    }

    @Override
    public View getCompleteButton() {
        return null;
    }

    //------------------------------------------点击事件---------------------------------------------

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.popup_choose_from_camera:
                //从相机读取
                CameraUtils.takePhotos(c,f);
                dismiss();
                break;
            case R.id.popup_choose_from_album:
                //从相册读取
                CameraUtils.takePhotoFromAlbum(c,f);
                dismiss();
                break;
        }

    }

}
