package com.min.mylibrary.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.min.mylibrary.R;

/**
 * Created by Min on 2016/11/29.
 * 底部dialog
 * 图片选择
 */

public class BottomMenuDialog extends Dialog implements View.OnClickListener {

    private Button btn_photograph,btn_photo,btn_cancel;
    private String photographText,localphotoText,cancelText;

    private View.OnClickListener photographListener;
    private View.OnClickListener cancelListener;
    private View.OnClickListener localphotoListener;


    public BottomMenuDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    public BottomMenuDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * @param context
     */
    public BottomMenuDialog(Context context, String photographText, String localphotoText) {
        super(context, R.style.dialogFullscreen);
        this.photographText = photographText;
        this.localphotoText = localphotoText;
    }

    /**
     * @param context
     */
    public BottomMenuDialog(Context context, String photographText, String localphotoText, String cancelText) {
        super(context, R.style.dialogFullscreen);
        this.photographText = photographText;
        this.localphotoText = localphotoText;
        this.cancelText = cancelText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bottom);
        Window window=getWindow();
        WindowManager.LayoutParams layoutParams=window.getAttributes();
        layoutParams.flags=WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount=0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_photograph= (Button) findViewById(R.id.btn_photograph);
        btn_photo= (Button) findViewById(R.id.btn_photo);
        btn_cancel= (Button) findViewById(R.id.btn_cancel);

        if(!TextUtils.isEmpty(photographText)){
            btn_photograph.setText(photographText);
        }
        if(!TextUtils.isEmpty(localphotoText)){
            btn_photo.setText(localphotoText);
        }
        if(!TextUtils.isEmpty(cancelText)){
            btn_cancel.setText(cancelText);
        }

        btn_photograph.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.btn_photograph){
            if(photographListener!=null){
                photographListener.onClick(view);
            }
            return;
        }
        if(id==R.id.btn_photo){
            if(localphotoListener!=null){
                localphotoListener.onClick(view);
            }
            return;
        }
        if(id==R.id.btn_cancel){
            if(cancelListener!=null){
                cancelListener.onClick(view);
            }
            return;
        }
    }

    public View.OnClickListener getPhotographListener() {
        return photographListener;
    }

    public void setPhotographListener(View.OnClickListener photographListener) {
        this.photographListener = photographListener;
    }

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public View.OnClickListener getLocalphotoListener() {
        return localphotoListener;
    }

    public void setLocalphotoListener(View.OnClickListener localphotoListener) {
        this.localphotoListener = localphotoListener;
    }
}
