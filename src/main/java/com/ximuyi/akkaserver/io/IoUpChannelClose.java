package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.message.MsCallback;


public class IoUpChannelClose extends IoUpMessage implements MsCallback {

    private final boolean notifyClient;

    public IoUpChannelClose(boolean notifyClient, String reason) {
        super(CommandConst.CHANNEL_CLOSE, reason == null ? "UNKNOW" : reason);
        this.notifyClient = notifyClient;
    }

    public boolean isNotifyClient() {
        return notifyClient;
    }

    public String getReason() {
        return (String)getMessage();
    }

    @Override
    public void onCallback(short resultCode) {
        //验证结果而已
        System.out.println(resultCode);
    }
}
