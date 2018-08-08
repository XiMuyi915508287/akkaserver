package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.IUser;
import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.session.AkkaUserManager;
import com.ximuyi.akkaserver.session.ISession;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IoUtil {

    /**
     * @param extUser
     * @param cmd
     * @param resultCode
     * @param message
     */
    public static void send(IUser extUser, ICommand cmd, short resultCode, Object message){
        ISession session = AkkaUserManager.getInstance().getSession(extUser.getUserId());
        if (session == null){
            return;
        }
        session.sendIoMessage(new IoDownMessage(cmd, resultCode, message));
    }

//
//    public static void send(Stream<IUser> stream, ICommand cmd, short resultCode, Object message){
//        List<ISession> sessions = new ArrayList<>();
//        stream.forEach( extUser->{
//            ISession session = AkkaUserManager.getInstance().getSession(extUser.getUserId());
//            if (session == null){
//                return;
//            }
//            sessions.add(session);
//        });
//        send(sessions, cmd, resultCode, message);
//    }


    /**
     * @param extUsers
     * @param cmd
     * @param resultCode
     * @param message
     */
    public static void send(Collection<? extends IUser> extUsers, ICommand cmd, short resultCode, Object message){
        List<ISession> sessions = new ArrayList<>(extUsers.size());
        extUsers.forEach( extUser->{
            ISession session = AkkaUserManager.getInstance().getSession(extUser.getUserId());
            if (session == null){
                return;
            }
            sessions.add(session);
        });
        send(sessions, cmd, resultCode, message);
    }

    private static void send(List<ISession> sessions, ICommand cmd, short resultCode, Object message){
        if (sessions.size() == 0){
            return;
        }
        if (sessions.size() == 1){
            sessions.get(0).sendIoMessage(new IoDownMessage(cmd, resultCode, message));
        }
        else {
            ByteBuf byteBuf = new IoDownMessage(cmd, resultCode, message).toByteBuf();
            if (byteBuf != null){
                IoDownShared shared = new IoDownShared(cmd, byteBuf);
                sessions.forEach( session -> session.sendIoMessage(shared));
            }
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
