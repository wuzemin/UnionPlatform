package com.min.smalltalk.server.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Min on 2016/11/23.
 *[A brief description]
 * <p/>
 * //在任何地方发送广播
 * BroadcastManager.getInstance(mContext).sendBroadcast(FindOrderActivity.ACTION_RECEIVE_MESSAGE);
 * <p/>
 * //页面在oncreate中初始化广播
 * BroadcastManager.getInstance(mContext).addAction(ACTION_RECEIVE_MESSAGE, new BroadcastReceiver(){
 *
 * @author huxinwu
 * @version 1.0
 * @Override public void onReceive(Context arg0, Intent intent) {
 * String command = intent.getAction();
 * if(!TextUtils.isEmpty(command)){
 * if((ACTION_RECEIVE_MESSAGE).equals(command)){
 * //获取json结果
 * String json = intent.getStringExtra("result");
 * //做你该做的事情
 * }
 * }
 * }
 * });
 * <p/>
 * //页面在ondestory销毁广播
 * BroadcastManager.getInstance(mContext).destroy(ACTION_RECEIVE_MESSAGE);
 */

public class BroadcastManager {
    private Context context;
    private static BroadcastManager instance;
    private Map<String,BroadcastReceiver> receiverMap;

    public BroadcastManager(Context context) {
        this.context = context;
        receiverMap=new HashMap<String,BroadcastReceiver>();
    }
    /**
     * 获取BroadcastManager实例，单例模式实现
     */
    public static BroadcastManager getInstance(Context context){
        if(instance==null){
//            synchronized (AsyncTaskManager.class){
                if(instance==null){
                    instance=new BroadcastManager(context);
                }
//            }
        }
        return instance;
    }
    /**
     * 添加
     */
    public void addAction(String action,BroadcastReceiver receiver){
        IntentFilter filter=new IntentFilter();
        filter.addAction(action);
//        context.registerReceiver(receiver,filter);
        receiverMap.put(action,receiver);
    }
    /**
     * 发送广播
     */
    public void sendBroadcast(String action){
        sendBroadcast(action);
    }
    /**
     * 销毁广播
     */
    public void destroy(String action){
        if(receiverMap!=null){
            BroadcastReceiver receiver=receiverMap.get(action);
            if(receiver!=null){
                context.unregisterReceiver(receiver);
            }
        }
    }
}
