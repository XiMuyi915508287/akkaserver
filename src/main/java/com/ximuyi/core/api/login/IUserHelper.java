package com.ximuyi.core.api.login;

import com.ximuyi.core.user.IUser;

public interface IUserHelper<T0> {

    /**
     * 登陆的时候检测
     * @param message
     * @return
     */
    LoginResult doUserLogin(T0 message, boolean isReconnect);

    long getUserId(T0 message);

    void onReconnect(IUser extUser, ConnectWay connectWay);

    void onDisconnect(IUser extUser);

    void onUserLogin(IUser extUser);

    void onUserLogout(IUser extUser);
}
