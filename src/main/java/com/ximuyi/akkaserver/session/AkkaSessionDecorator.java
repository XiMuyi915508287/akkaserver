package com.ximuyi.akkaserver.session;

import akka.actor.ActorRef;
import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.io.IoDownPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkaSessionDecorator implements ISession {

    private static final Logger logger = LoggerFactory.getLogger(AkkaSessionDecorator.class);

    private ActorRef actorRef;
    private AkkaUser akkaUser;

    public AkkaSessionDecorator() {
    }

    /**
     * 新建AkkaUser得到ActorRef的时候调用 (1)
     * (1) 与 (2) 存在并发的情况
     * @param actorRef
     */
    protected void setActorRef(ActorRef actorRef) {
        if (akkaUser != null && actorRef != akkaUser.getSelf()){
            throw new UnsupportedOperationException(actorRef + " != " + akkaUser.getSelf());
        }
        this.actorRef = actorRef;
    }

    /**
     * AkkaUser构造的时候调用(2)
     * (1) 与 (2) 存在并发的情况
     * @param akkaUser
     */
    protected void setAkkaUser(AkkaUser akkaUser) {
        if (this.akkaUser != null){
            logger.debug("akkaUser is already exist, userId={}", this.akkaUser.getUserId());
            return;
        }
        if (actorRef != null && actorRef != akkaUser.getSelf()){
            throw new UnsupportedOperationException(actorRef + " != " + akkaUser.getSelf());
        }
        this.akkaUser = akkaUser;
        this.actorRef = akkaUser.getSelf();
    }

    @Override
    public boolean sendIoMessage(IoDownPackage message) {
        if (akkaUser != null){
            return akkaUser.sendIoMessage(message);
        }
        actorRef.tell(message, actorRef);
        return true;
    }

    @Override
    public IAkkaUser getAkkaUser() {
        return akkaUser;
    }

    @Override
    public boolean isLogin() {
        return akkaUser == null ? false : akkaUser.isLogin();
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        actorRef.tell(message, actorRef);
    }

    @Override
    public ActorRef getSelf() {
        return actorRef;
    }
}
