package com.ximuyi.akkaserver.api.login;

import com.ximuyi.akkaserver.IUser;

public class LoginResult {
    //返回给前端的信息，内容自己定义
    private Object message;
    private final boolean isSuccess;
    private final String failReason;
    private final IUser user;

    public LoginResult(boolean isSuccess, String failReason, IUser user) {
        this.isSuccess = isSuccess;
        this.failReason = failReason;
        this.user = user;
    }

    public Object getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getFailReason() {
        return failReason;
    }

    public IUser getUser() {
        return user;
    }
}
