package com.ximuyi.core.api.login;

public class AccountInfo {

    private final long userId;
    private final String account;

    public AccountInfo(long userId, String account) {
        this.userId = userId;
        this.account = account;
    }

    public long getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }
}
