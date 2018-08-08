package com.ximuyi.akkaserver.message;

import com.ximuyi.akkaserver.session.ISession;

public class MsAkkaUserLogin {
    private final ISession session;

    public MsAkkaUserLogin(ISession session) {
        this.session = session;
    }

    public ISession getSession() {
        return session;
    }
}
