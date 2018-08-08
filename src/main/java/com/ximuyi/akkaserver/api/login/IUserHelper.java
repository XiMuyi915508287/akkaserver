package com.ximuyi.akkaserver.api.login;

import com.ximuyi.akkaserver.IUser;

public interface IUserHelper {

    void onReConnection(IUser extUser, boolean isLogin);

    void onDisConnection(IUser extUser);

    void onLogin(IUser extUser);

    void onLogout(IUser extUser);
}
