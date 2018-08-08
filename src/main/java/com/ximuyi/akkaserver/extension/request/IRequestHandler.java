package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.session.ISession;

public abstract class IRequestHandler<T0> {

    public abstract void handleRequest(ISession session, T0 messsage);

    public abstract ICommand getCmd();

    public boolean isThreadSafe(){
        return false;
    }
}
