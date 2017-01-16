package com.min.smalltalk.wedget;

import android.content.Context;
import android.text.TextUtils;

import com.min.mylibrary.util.T;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Min on 2016/12/2.
 */

public class Operation {
    public static void setConversationTop(final Context context, Conversation.ConversationType conversationType, String targetId, boolean state) {
        if (!TextUtils.isEmpty(targetId) && RongIM.getInstance() != null) {
            RongIM.getInstance().setConversationToTop(conversationType, targetId, state, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
//                    NToast.shortToast(context, "设置成功");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    T.showShort(context, "设置失败");
                }
            });
        }
    }

    public static void setConverstionNotif(final Context context, Conversation.ConversationType conversationType, String targetId, boolean state) {
        Conversation.ConversationNotificationStatus cns;
        if (state) {
            cns = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
        } else {
            cns = Conversation.ConversationNotificationStatus.NOTIFY;
        }
        RongIM.getInstance().setConversationNotificationStatus(conversationType, targetId, cns, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
//                    NToast.shortToast(context, "设置免打扰成功");
                } else if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
//                    NToast.shortToast(context, "取消免打扰成功");
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                T.showShort(context, "设置失败");
            }
        });
    }
}
