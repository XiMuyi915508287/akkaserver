package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestHandler<T0> extends IRequestHandler<T0> {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final ICommand cmd;

    public RequestHandler(ICommand extCmd) {
        this.cmd = extCmd;
    }

    @Override
    public void handleRequest(ISession session, T0 messsage) {
        try {
            String reason = validate(session);
            if (reason != null){
                logger.error("{} invalid request reason:{}", getCmd().toString(), reason);
            }
            _handleRequest(session, messsage);
        }
        catch (Throwable t){
            logger.error("{} handle request error.", getCmd().toString(), t);
        }
    }

    @Override
    public final ICommand getCmd() {
        return cmd;
    }

    protected abstract void _handleRequest(ISession session, T0 messsage) throws Throwable;

    protected abstract String validate(ISession session);
}
