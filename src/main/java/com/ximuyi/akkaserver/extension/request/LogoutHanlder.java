package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.session.ISession;

public class LogoutHanlder extends RequestHandler<Void>  {

    public LogoutHanlder() {
        super(CommandConst.LOGOUT);
    }

    @Override
    protected final void _handleRequest(ISession session, Void messsage) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final String validate(ISession session) {
        return session.isLogin() ?  null : "unlogin";
    }
}
