package com.ximuyi.akkaserver.net.netty;

import com.ximuyi.akkaserver.component.ReceiveAction;
import com.ximuyi.akkaserver.config.ConfigKey;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.net.NetworkService;
import com.ximuyi.akkaserver.thread.PoolThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyService extends NetworkService {

    private static final Logger logger = LoggerFactory.getLogger(NettyService.class);

    private EventLoopGroup bossGroup;//boss线程 负责创建、连接、绑定socket
    private EventLoopGroup workerGroup;//执行所有异步IO
    private ServerBootstrap boot;

    public NettyService() {
        super(NettyService.class.getName());
    }

    @Override
    public void init(ConfigWrapper config) throws Throwable {
        super.init(config);
        int bossThredNum = config.getInteger(ConfigKey.NETTY_BOSS_THREAD);
        int workerThreadNum = config.getInteger(ConfigKey.NETTY_CHILD_THREAD);
        this.bossGroup = new NioEventLoopGroup(bossThredNum, new PoolThreadFactory("netty-parent", true));
        this.workerGroup = new NioEventLoopGroup(workerThreadNum, new PoolThreadFactory("netty-child", true));
        this.boot = initBootStrap();
        int port = config.getInteger(ConfigKey.NETTY_LISTEN_PORT);
        ChannelFuture future = boot.bind(port).sync();
        Thread thread = new PoolThreadFactory("netty-close", true).newThread( ()->{
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("", e);
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Override
    public void destroy() {
        super.destroy();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    protected void buildSycReceive(ReceiveAction receive) {

    }

    private ServerBootstrap initBootStrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.DEBUG));

        bootstrap.childHandler(new NettyInitializer());
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
