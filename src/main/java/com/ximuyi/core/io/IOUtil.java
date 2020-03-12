package com.ximuyi.core.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.response.SharedResponse;
import com.ximuyi.core.session.SessionManager;
import com.ximuyi.core.user.IUser;
import com.ximuyi.core.user.IUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtil {

    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * @param extUser
     * @param command
     * @param resultCode
     * @param message
     */
    public static void userResponse(IUser extUser, ICommand command, ResultCode resultCode, Object message){
        IUserSession session = SessionManager.getInstance().getUserSession(extUser.getUserId());
        if (session == null){
            return;
        }
        session.cacheAndResponse(command, resultCode, message);
    }

    /**
     *
     * @param session
     * @param command
     * @param resultCode
     * @param message
     */
    public static void sessionResponse(IUserSession session, ICommand command, ResultCode resultCode, Object message){
        session.cacheAndResponse(command, resultCode, message);
    }

    /**
     * @param extUsers
     * @param cmd
     * @param resultCode
     * @param message
     */
    public static void sharedUserResponse(Collection<? extends IUser> extUsers, ICommand cmd, ResultCode resultCode, Object message){
        List<IUserSession> sessions = new ArrayList<>(extUsers.size());
        extUsers.forEach( extUser->{
            IUserSession session = SessionManager.getInstance().getUserSession(extUser.getUserId());
            if (session == null){
                return;
            }
            sessions.add(session);
        });
        sharedSessionResponse(sessions, cmd, resultCode, message);
    }

    /**
     *
     * @param sessions
     * @param command
     * @param resultCode
     * @param message
     */
    private static void sharedSessionResponse(List<IUserSession> sessions, ICommand command, ResultCode resultCode, Object message){
        if (sessions.size() == 0){
            return;
        }
        try {
            SharedResponse sharedResponse = new SharedResponse(command, resultCode, message);
            sessions.forEach( session -> session.sharedResponse(sharedResponse));
        }
        catch (Throwable t){
            logger.error("message error:{}", message, t);
        }
    }
}
