package com.min.smalltalk.listener;

import com.min.smalltalk.wedget.Wheel.WheelView;

/**
 * Created by Min on 2016/12/7.
 */

public interface OnWheelChangedListener {
    void onChanged(WheelView wheel, int oldValue, int newValue);
}
