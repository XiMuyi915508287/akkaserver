package com.ximuyi.akkaserver;

public interface IUser extends IUserId{

    String getAccount();

    boolean isLogin();
}
