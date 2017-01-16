package com.min.smalltalk.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.min.smalltalk.R;

import java.util.List;

/**
 * Created by Min on 2016/12/19.
 */

public abstract class BasePopup extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "BASE POPUP";
    protected View view;//容纳弹窗的view
    protected PopupWindow mPopupWindow;//弹窗
    protected List<Object> list;
    private Activity mActivity;

    private View cancelButton;
    private View completeButton;

    private OnDismissListener listener;
    private OnCompleteClickListener mOnCompleteClickListener;

    public BasePopup(Activity activity) {
        this.mActivity = activity;
        view = getView();
        view.setFocusableInTouchMode(true);

        cancelButton=getCancelButton();
        completeButton=getCompleteButton();
        if (cancelButton!=null)
            cancelButton.setOnClickListener(this);
        if (completeButton!=null)
            completeButton.setOnClickListener(this);
        mPopupWindow =new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(0);
        setTitleText();
    }
    public BasePopup(Activity activity,int Width,int Height) {
        this.mActivity = activity;
        view = getView();
        view.setFocusableInTouchMode(true);

        cancelButton=getCancelButton();
        completeButton=getCompleteButton();
        if (cancelButton!=null)
            cancelButton.setOnClickListener(this);
        if (completeButton!=null)
            completeButton.setOnClickListener(this);
        mPopupWindow =
                new PopupWindow(view, Width, Height);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(0);
        setTitleText();
    }
    public BasePopup(Activity activity,List<Object> data,int Width,int Height) {
        this(activity,Width,Height);
        this.list=data;
    }


    public void showPopupWindow() {
        try {
            mPopupWindow.showAtLocation(mActivity.findViewById(android.R.id.content),
                    Gravity.RIGHT | Gravity.CENTER_HORIZONTAL, 0, 0);
            if (setAnima()!=null&&view!=null){
                view.findViewById(R.id.popup_base).startAnimation(setAnima());
            }
        } catch (Exception e) {
            Log.e(TAG, "Show Popup Error1");
        }
    }

    public void showPopupWindow(int resource) {
        try {
            mPopupWindow.showAtLocation(mActivity.findViewById(resource),
                    Gravity.RIGHT | Gravity.CENTER_HORIZONTAL, 0, 0);
            if (setAnima()!=null&&view!=null){
                view.findViewById(R.id.popup_base).startAnimation(setAnima());
            }
        } catch (Exception e) {
            Log.e(TAG, "Show Popup Error2");
        }
    }

    public void showPopupWindow(View view) {
        try {
            mPopupWindow.showAsDropDown(view);
            if (setAnima()!=null&&view!=null){
                view.findViewById(R.id.popup_base).startAnimation(setAnima());
            }
        } catch (Exception e) {
            Log.e(TAG, "Show Popup Error3");
        }
    }

    /** 设置弹框大小 */
    public void setPopupSize(int width, int height) {
        mPopupWindow = new PopupWindow(view, width, height);
    }

    public void dismiss() {
        try {
            mPopupWindow.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "Dismiss Popup Error4");
        }
    }

    public View inflateView(int layoutID){
        return LayoutInflater.from(mActivity).inflate(layoutID,null);
    }

    //------------------------------------------抽象方法-----------------------------------------------
    public abstract void setTitleText();
    public abstract View getView();
    public abstract Animation setAnima();
    public abstract View getCancelButton();
    public abstract View getCompleteButton();

    //------------------------------------------动画-----------------------------------------------

    /**
     * 得到从底部滑动出来的动画
     */
    protected Animation getTransAnimaFromBottom() {
        Animation translateAnimation =
                new TranslateAnimation(0, 0, getScreenHeight(mActivity), 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillBefore(true);
        return translateAnimation;
    }

    public  int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 得到自定义位移动画
     */
    protected Animation getTransAnimaCustom(int fromX, int toX, int fromY, int toY) {
        Animation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration(300);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillBefore(true);
        return translateAnimation;
    }

    /**
     * 得到缩放动画
     */
    protected Animation getScaleAnimation() {
        Animation scaleAnimation =
                new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillBefore(true);
        return scaleAnimation;
    }


    @Override
    public void onClick(View v) {
        if (null!=cancelButton&&v==cancelButton)
            mPopupWindow.dismiss();
        if (null!=completeButton&&v==completeButton){
            if (mOnCompleteClickListener!=null)
                mOnCompleteClickListener.onCompleteClick(v);
        }
    }

    //------------------------------------------Setter-----------------------------------------------

    public void setOnDismissListener(final OnDismissListener listener) {
        this.listener = listener;
        if (listener != null) {
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    listener.onDismiss();
                }
            });
        }
    }

    public void setOnCompleteClickListener(OnCompleteClickListener onCompleteClickListener) {
        mOnCompleteClickListener = onCompleteClickListener;
    }

    //------------------------------------------接口-----------------------------------------------
    public interface OnDismissListener {
        void onDismiss();
    }
    public interface OnCompleteClickListener{
        void onCompleteClick(View v);
    }
}
