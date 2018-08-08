package com.ximuyi.akkaserver.session;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.InvalidActorNameException;
import akka.actor.Props;
import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.IUser;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.session.channel.ChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AkkaUserManager {

    private static final String NAME = "akkaUsers";

    private static final Logger logger = LoggerFactory.getLogger(AkkaUserManager.class);

    private static AkkaUserManager instance = new AkkaUserManager();

    public static AkkaUserManager getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<Long, AkkaSessionDecorator> userId2Users = new ConcurrentHashMap<>();

    /**
     * 不能引用其他地方的ActorContext，不然会报错的
     * */
    private final IAkkaUserLife akkaUserLife;

    private AkkaUserManager() {
        this.akkaUserLife = new AkkaUserLife();
    }

    /**
     * 暂时这样子写，这个函数还存在很多问题
     * @param session
     * @param exUser
     * @return
     */
    public AkkaUserCreate createUser(ChannelSession session, IUser exUser){
        IAkkaUser akkaUser = getIAkkaUser(exUser.getUserId());
        if (akkaUser != null){
            return new AkkaUserCreate(true, akkaUser.getSelf());
        }
        AkkaSessionDecorator decorator = new AkkaSessionDecorator();
        AkkaSessionDecorator decorator0 = userId2Users.putIfAbsent(exUser.getUserId(), decorator);
        if (decorator0 == null){
            ActorSystem system = ContextResolver.getActorSystem();
            //Akka的对象创建（构造函数的调用）是异步的
            try {
                ActorRef actorRef = system.actorOf(Props.create(AkkaUser.class, session, exUser, akkaUserLife), "user-"+exUser.getUserId());
                decorator.setActorRef(actorRef);
                logger.info("create user actor {} being attached channel {}", actorRef.path().toString(), session.getUniqueId());
                return new AkkaUserCreate(false, actorRef);
            }
            catch (InvalidActorNameException t){
                //原理上，这里不可能报错了
                logger.error("create user actor {} error", exUser.getUserId(), t);
            }
        }
        else {
            decorator = decorator0;
        }
        //但是移除只可能是创建出来的Akkactor自己移除的，除非是顶号的玩家才到这一步
        return new AkkaUserCreate(true, decorator.getSelf());
    }

    public IAkkaUser getIAkkaUser(long userId) {
        AkkaSessionDecorator decorator = userId2Users.get(userId);
        return  decorator != null ? decorator.getAkkaUser() : null;
    }

    public ISession getSession(long userId) {
        return userId2Users.get(userId);
    }

    public List<AkkaSessionDecorator> getAllSeesion() {
        return new ArrayList<>(userId2Users.values());
    }

    public int getOnlineCount() {
        return userId2Users.size();
    }

    protected void remove(AkkaUser akkaUser) {
        //Actor会不会存在没有调用PreStop的情况呢？
        AkkaSessionDecorator decorator = userId2Users.remove(akkaUser.getUserId());
        logger.info("create user actor {} has removed akkaUser {}", decorator.getSelf().path().toString(), akkaUser.getUserId());
    }

    protected void add(AkkaUser akkaUser) {
        AkkaSessionDecorator decorator = userId2Users.get(akkaUser.getUserId());
        decorator.setAkkaUser(akkaUser);
        logger.info("create user actor {} has attached akkaUser {}", decorator.getSelf().path().toString(), akkaUser.getUserId());
    }
}
