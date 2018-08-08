package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.session.ISession;

public class EmptyHanlder extends RequestHandler<Void>  {

    public EmptyHanlder(ICommand extCmd) {
        super(extCmd);
    }

    @Override
    protected final void _handleRequest(ISession session, Void messsage) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final String validate(ISession session) {
        return "unsupport";
    }
}
