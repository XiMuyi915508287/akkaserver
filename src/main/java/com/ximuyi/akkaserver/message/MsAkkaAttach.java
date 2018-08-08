package com.ximuyi.akkaserver.message;

import com.ximuyi.akkaserver.session.AkkaUser;

public class MsAkkaAttach {
    private final AkkaUser akkaUser;

    public MsAkkaAttach(AkkaUser akkaUser) {
        this.akkaUser = akkaUser;
    }

    public AkkaUser getAkkaUser() {
        return akkaUser;
    }
}
