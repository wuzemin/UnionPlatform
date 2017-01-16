package com.min.smalltalk.listener;

import com.min.smalltalk.wedget.Wheel.WheelView;

/**
 * Created by Min on 2016/12/7.
 */

public interface OnWheelScrollListener {
    void onScrollingStarted(WheelView wheel);

    void onScrollingFinished(WheelView wheel);
}
