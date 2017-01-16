package com.min.smalltalk.server;

import android.content.Context;

/**
 * Created by Min on 2017/1/15.
 */

public class BaseAction {
    protected Context mContext;
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BaseAction(Context context) {
        this.mContext = context;
    }
}
