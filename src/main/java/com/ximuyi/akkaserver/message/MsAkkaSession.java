package com.ximuyi.akkaserver.message;

import com.ximuyi.akkaserver.session.ISession;

public class MsAkkaSession implements MsCallback{

    private final ISession session;
    private MsCallback msCallback;

    public MsAkkaSession(ISession session) {
        this.session = session;
    }

    public ISession getSession() {
        return session;
    }

    @Override
    public void onCallback(short resultCode) {
        msCallback.onCallback(resultCode);
    }

    public MsAkkaSession setMsCallback(MsCallback msCallback) {
        this.msCallback = msCallback;
        return this;
    }
}
