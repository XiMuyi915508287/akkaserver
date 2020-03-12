package com.ximuyi.core.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.IUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public List<UserSession> getAllUserSessions() {
        return new ArrayList<>(userSessions.values());
    }

    public int getSessionCount() {
        return userSessions.size();
    }

    public UserSession getUserSession(long userId) {
        return userSessions.get(userId);
    }

    protected UserSession removeUserSession(long userId) {
        UserSession session = userSessions.remove(userId);
        if (session != null){
            info(session, "session removed");
        }
        return session;
    }

    protected UserSession addUserSession(UserSession session) {
        IUser user = session.getUser();
        UserSession session1 = userSessions.put(user.getUserId(), session);
        info(session, "session added");
        if (session1 != null){
            info(session1, "session replaced");
        }
        return session1;
    }

    private void info(UserSession session, String message){
        ActorRef actorRef = session.getSelf();
        IUser user = session.getUser();
        NetChannel netChannel = session.getNetChannel();
        logger.info("{}, userId:{} account:{} channel:{} actor:{}", message, user.getUserId(), user.getAccount(), netChannel.getUniqueId(),
                actorRef.path().name());
    }
}
