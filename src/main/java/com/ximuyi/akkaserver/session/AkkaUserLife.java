package com.ximuyi.akkaserver.session;

public class AkkaUserLife implements IAkkaUserLife {
    @Override
    public void onCreate(AkkaUser akkaUser) {
        AkkaUserManager.getInstance().add(akkaUser);
    }

    @Override
    public void onRemove(AkkaUser akkaUser) {
        AkkaUserManager.getInstance().remove(akkaUser);
    }
}
