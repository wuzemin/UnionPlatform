package com.min.smalltalk.listener;

import com.min.smalltalk.bean.LocationEntity;

/**
 * Created by Min on 2016/12/21.
 * 逆地理编码或者定位完成后回调接口
 */

public interface OnLocationGetListener {
    void onLocationGet(LocationEntity entity);

    void onRegecodeGet(LocationEntity entity);
}
