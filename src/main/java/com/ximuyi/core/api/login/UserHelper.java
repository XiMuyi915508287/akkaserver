package com.ximuyi.core.api.login;

import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.user.IUser;

public class UserHelper implements IUserHelper<Void> {

    @Override
    public LoginResult doUserLogin(Void message, boolean isReconnect) {
        return new LoginResult(null, ConnectWay.Inside, ResultCode.UNKNOWN, null);
    }

    @Override
    public long getUserId(Void message) {
        return 0;
    }

    @Override
    public void onReconnect(IUser extUser, ConnectWay connectWay) {
    }

    @Override
    public void onDisconnect(IUser extUser) {
    }

    @Override
    public void onUserLogin(IUser extUser) {
    }

    @Override
    public void onUserLogout(IUser extUser) {
    }
}
