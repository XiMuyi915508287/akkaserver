package com.ximuyi.core.session;

import java.util.concurrent.ConcurrentHashMap;

import com.ximuyi.core.user.IUserSession;

public class AkkaUserContext {

    private static final ThreadLocal<AkkaUserContext> LOCAL = new ThreadLocal<AkkaUserContext>();

    private final ConcurrentHashMap<Integer, Object> cacheMap;

    private final IUserSession session;

    public AkkaUserContext(IUserSession session) {
        this.session = session;
        this.cacheMap = new ConcurrentHashMap<>();
    }

    public static void clear() {
        LOCAL.remove();
    }

    public static void init(IUserSession session) {
        LOCAL.set(new AkkaUserContext(session));
    }

    public static boolean isAkkaThread(long userId){
        AkkaUserContext context = LOCAL.get();
        return context != null && context.session.getUserId() == userId;
    }
}
