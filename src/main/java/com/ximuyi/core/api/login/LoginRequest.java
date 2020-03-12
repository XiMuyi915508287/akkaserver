package com.ximuyi.core.api.login;

public class LoginRequest {
    private final Long userId;
    private final short platform;
    public final String account;

    public LoginRequest(Long userId, short platform, String account) {
        this.userId = userId;
        this.platform = platform;
        this.account = account;
    }

    public Long getUserId() {
        return userId;
    }

    public short getPlatform() {
        return platform;
    }

    public String getAccount() {
        return account;
    }
}
