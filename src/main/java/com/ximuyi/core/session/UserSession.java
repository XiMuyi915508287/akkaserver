package com.ximuyi.core.session;

import java.util.Objects;

import akka.actor.ActorRef;
import com.ximuyi.core.actor.ActorRunnable;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.response.ByteResponse;
import com.ximuyi.core.command.response.SharedResponse;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.IUser;
import com.ximuyi.core.user.IUserSession;
import com.ximuyi.core.user.InnerUser;

public class UserSession implements IUserSession {

    private final ActorRef akkaUser;
    private final InnerUser innerUser;
    private NetChannel netChannel;

    public UserSession(ActorRef akkaUser, InnerUser innerUser, NetChannel netChannel) {
        this.akkaUser = akkaUser;
        this.innerUser = innerUser;
        this.setNetChannel(netChannel);
    }

    public NetChannel getNetChannel() {
        return netChannel;
    }

    public long getChannelUniqueId(){
        return netChannel.getUniqueId();
    }

    public void setNetChannel(NetChannel netChannel) {
        this.netChannel = netChannel;
        this.netChannel.setBindUserId(innerUser.getUserId());
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    @Override
    public IUser getUser() {
        return innerUser.getExUser();
    }

    @Override
    public ActorRef getSelf() {
        return akkaUser;
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        akkaUser.tell(message, sender);
    }

    @Override
    public void cacheAndResponse(ICommand command, ResultCode resultCode, Object message) {
        if (AkkaUserContext.isAkkaThread(innerUser.getUserId())) {
            long uniqueId = innerUser.incCommandIdAndGet();
            ByteResponse byteResponse = new ByteResponse(command, uniqueId, resultCode, message);
            netChannel.addCommandResponse(byteResponse);
        }
        else {
            ActorRunnable runnable = new ActorRunnable(akkaUser,"cacheAndResponse", () -> cacheAndResponse(command, resultCode, message));
            akkaUser.tell(runnable, akkaUser);
        }
    }

    @Override
    public void sharedResponse(SharedResponse sharedResponse) {
        netChannel.addCommandResponse(sharedResponse);
    }

    /**
     * 只要UserId一样就可以，不同的channel对业务屏蔽
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return innerUser.getUserId() == that.getUserId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerUser.getUserId());
    }
}
