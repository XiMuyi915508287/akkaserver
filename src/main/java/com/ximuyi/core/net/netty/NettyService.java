package com.ximuyi.core.net.netty;

import com.ximuyi.core.config.ConfigKey;
import com.ximuyi.core.config.Configs;
import com.ximuyi.core.net.NetworkService;
import com.ximuyi.core.thread.PoolThreadFactory;
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

    private static int BOSS_THREAD_NUM = Configs.getInstance().getInteger(ConfigKey.NETTY_BOSS_THREAD);
    private static int WORKER_THREAD_NUM = Configs.getInstance().getInteger(ConfigKey.NETTY_CHILD_THREAD);

    private EventLoopGroup bossGroup;//boss线程 负责创建、连接、绑定socket
    private EventLoopGroup workerGroup;//执行所有异步IO
    private ServerBootstrap serverBootstrap;

    public NettyService() {
        super(NettyService.class.getName());
    }

    @Override
    public void init() throws Throwable {
        super.init();
        Configs config = Configs.getInstance();
        this.bossGroup = new NioEventLoopGroup(BOSS_THREAD_NUM, new PoolThreadFactory("netty-parent", true));
        this.workerGroup = new NioEventLoopGroup(WORKER_THREAD_NUM, new PoolThreadFactory("netty-child", true));
        this.serverBootstrap = initBootStrap();
        int port = config.getInteger(ConfigKey.NETTY_LISTEN_PORT);
        ChannelFuture future = serverBootstrap.bind(port).sync();
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
