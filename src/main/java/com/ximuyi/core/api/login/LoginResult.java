package com.ximuyi.core.api.login;

import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.user.IUser;

public class LoginResult {
    private final IUser extUser;
    private final ResultCode resultCode;
    private final ConnectWay connectWay;
    private final Object message;

    public LoginResult(IUser extUser, ConnectWay connectWay, ResultCode resultCode, Object message)  {
        this.extUser = extUser;
        this.connectWay = connectWay;
        this.resultCode = resultCode;
        this.message = message;
    }

    public IUser getExtUser() {
        return extUser;
    }

    public Object getMessage() {
        return message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public ConnectWay getConnectWay() {
        return connectWay;
    }

    @Override
    public String toString() {
        return "{" +
                "extUser=" + extUser +
                ", resultCode=" + resultCode.code() +
                ", message=" + message +
                '}';
    }
}
