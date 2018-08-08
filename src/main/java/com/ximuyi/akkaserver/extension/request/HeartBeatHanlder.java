package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.io.IoDownMessage;
import com.ximuyi.akkaserver.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HeartBeatHanlder<T0, T1> extends RequestHandler<T0>  {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHanlder.class);

    public HeartBeatHanlder() {
        super(CommandConst.HEART_BEAT);
    }

    @Override
    protected  void _handleRequest(ISession session, T0 messsage) {
        T1 result = getMessage(messsage);
        session.sendIoMessage(new IoDownMessage(getCmd(), ResponseCode.SUCCESS, result));
    }

    @Override
    public boolean isThreadSafe() {
        return true;
    }

    @Override
    protected final String validate(ISession session) {
        return session.isLogin() ?  null : "unlogin";
    }

    protected abstract T1 getMessage(T0 messsage);
}
