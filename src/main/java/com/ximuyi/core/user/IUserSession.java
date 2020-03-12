package com.ximuyi.core.user;

import akka.actor.ActorRef;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.response.SharedResponse;

public interface IUserSession extends IUser {

    IUser getUser();

    ActorRef getSelf();

    void tell(Object message, ActorRef sender);

    default void tell(Object message){
        tell(message, getSelf());
    }

    void cacheAndResponse(ICommand command, ResultCode resultCode, Object message);

    void sharedResponse(SharedResponse sharedResponse);

    @Override
    default long getUserId(){
        return getUser().getUserId();
    }

    @Override
    default String getAccount() {
        return getUser().getAccount();
    }
}
