package com.min.mylibrary.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.min.mylibrary.R;

/**
 * Created by Min on 2016/11/17.
 */

public class LoadingDialog extends Dialog {
    private TextView tv;

    public LoadingDialog(Context context) {
        super(context, R.style.WinDialog);
        setContentView(R.layout.dialog);
        tv= (TextView) findViewById(R.id.show_message);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setText(String s){
        if(tv!=null){
            tv.setText(s);
            tv.setVisibility(View.VISIBLE);
        }
    }
    public void setText(int s){
        if(tv!=null){
            tv.setText(s);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            return false;
        }
        return super.onTouchEvent(event);
    }
}
