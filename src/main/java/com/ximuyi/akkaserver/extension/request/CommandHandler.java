package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.IUser;
import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.session.ISession;

public abstract class CommandHandler<T0> extends RequestHandler<T0> {

    public CommandHandler(ICommand extCmd) {
        super(extCmd);
    }

    @Override
    protected final void _handleRequest(ISession session, T0 messsage) throws Throwable {
        IAkkaUser akkaActor = session.getAkkaUser();
        IUser extUser = akkaActor.getExtUser();
        handleCommand(extUser, messsage);
    }

    @Override
    protected final String validate(ISession session) {
        return session.isLogin() ? null : "unlogin";
    }

    protected abstract void handleCommand(IUser extUser, T0 message) throws Throwable;
}
