package com.ximuyi.core.actor.message;

public class MsUserLogout {

    private final long userId;

    public MsUserLogout(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
