package com.ximuyi.akkaserver.extension.request;

import akka.actor.ActorRef;
import com.ximuyi.akkaserver.IUser;
import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.io.IoDownMessage;
import com.ximuyi.akkaserver.message.MsAkkaSession;
import com.ximuyi.akkaserver.message.MsAkkaUserLogin;
import com.ximuyi.akkaserver.session.AkkaUserCreate;
import com.ximuyi.akkaserver.session.AkkaUserManager;
import com.ximuyi.akkaserver.session.ISession;
import com.ximuyi.akkaserver.session.channel.ChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoginHanlder<T0, T1> extends RequestHandler<T0>  {

    private static final Logger logger = LoggerFactory.getLogger(LoginHanlder.class);

    public LoginHanlder() {
        super(CommandConst.LOGIN);
    }

    @Override
    protected final void _handleRequest(ISession session, T0 messsage) {
        ChannelSession chSession = (ChannelSession)session;
        LoginResult result = checkLogin(messsage);
        short responseCode = result.getResultCode();
        if (responseCode == ResponseCode.SUCCESS){
            AkkaUserCreate akkaUserCreate = AkkaUserManager.getInstance().createUser(chSession, result.getExtUser());
            ActorRef akkaUser = akkaUserCreate.getAkkaUser();
            if (akkaUser == null){
                responseCode = ResponseCode.RETRY_LOGIN;
            }
            else if (akkaUserCreate.isOld()){
                //原来已经登陆了
            }
            else {
                //正常途径的成功（默认是成功的）
                akkaUser.tell(new MsAkkaUserLogin(chSession), chSession.getSelf());
            }
            if (responseCode == ResponseCode.SUCCESS){
                //获取的Actor可能已经销毁了，接受不Session的消息，所以增加一个回调
                akkaUser.tell(new MsAkkaSession(chSession).setMsCallback( resultCode -> {
                    T1 message = resultCode == ResponseCode.SUCCESS ? result.getMessage() : null;
                    chSession.sendIoMessage(new IoDownMessage(getCmd(), resultCode, message));
                }), chSession.getSelf());
            }
        }
        else {
            chSession.sendIoMessage(new IoDownMessage(getCmd(), responseCode, null));
        }
        logger.debug("channel[{}] handle login resutCode {}", chSession.getUniqueId(), responseCode);

    }

    @Override
    protected final String validate(ISession session) {
        return session.isLogin() ? "isLogin" : null;
    }

    /**
     * @param messsage
     * @return
     */
    protected abstract LoginResult checkLogin(T0 messsage);


    public class LoginResult{
        private final IUser extUser;
        private final short resultCode;

        //不管失败或者成功，具体的信息在这里面
        private final T1 message;

        public LoginResult(IUser extUser, short resultCode, T1 message)  {
            this.extUser = extUser;
            this.resultCode = resultCode;
            this.message = message;
        }

        public IUser getExtUser() {
            return extUser;
        }

        public T1 getMessage() {
            return message;
        }

        public short getResultCode() {
            return resultCode;
        }
    }
}
