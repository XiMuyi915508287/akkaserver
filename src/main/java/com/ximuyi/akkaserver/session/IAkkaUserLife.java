package com.ximuyi.akkaserver.session;

public interface IAkkaUserLife {

    void onCreate(AkkaUser akkaUser);

    void onRemove(AkkaUser akkaUser);
}
