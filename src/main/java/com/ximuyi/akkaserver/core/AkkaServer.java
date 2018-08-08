package com.ximuyi.akkaserver.core;

import com.ximuyi.akkaserver.component.ComponentRegistry;
import com.ximuyi.akkaserver.component.IComponent;
import com.ximuyi.akkaserver.component.IComponentRegistry;
import com.ximuyi.akkaserver.config.ConfigKey;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.extension.request.RequestHandlerFactory;
import com.ximuyi.akkaserver.net.netty.NettyService;
import com.ximuyi.akkaserver.utils.ClassUtil;
import com.ximuyi.akkaserver.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.ximuyi.akkaserver.config.ConfigKey.SERVER_SCHEDULE;

public class AkkaServer {

    private static Logger logger = LoggerFactory.getLogger(AkkaServer.class);

    private static AkkaServer instance = null;

    public static final AkkaServer getInstance(){
        return instance;
    }

    private final int serverId;
    private final String appName;
    private AkkaAppContext application = null;
    private volatile boolean running = false;

    private AkkaServer(int serverId, String appName) {
        this.serverId = serverId;
        this.appName = appName;
    }

    private void init(String configPath) throws Throwable {
        ConfigWrapper wrapper = ConfigWrapper.read(configPath, "app.properties");
        ConfigWrapper.global(wrapper);
        IComponentRegistry serverComponents = new ComponentRegistry();
        IComponentRegistry namagerComponents = new ComponentRegistry();
        this.application = new AkkaAppContext(appName, wrapper, namagerComponents, serverComponents);
        this.application.setScheduler(ClassUtil.newInstance(wrapper.getString(SERVER_SCHEDULE)));
        this.application.setAppListener(ClassUtil.newInstance(wrapper.getString(ConfigKey.SERVER_APP_LITENER)));
        this.application.setUserHelper(ClassUtil.newInstance(wrapper.getString(ConfigKey.SERVER_LOGIN_HELPER)));
        this.application.setRequestHandlerFactory(new RequestHandlerFactory());
        ContextResolver.setContext(application);
        CoreAccessor.setLocator(new CoreLocatorImpl());
        this.initComponent(wrapper, namagerComponents, serverComponents);
        this.initConfigComponent(wrapper);

        //先初始化自己的组件，在初始化App，不然App获取某些组件会null
        application.getAppListener().onInit(wrapper);
    }

    private void initComponent( ConfigWrapper wrapper, IComponentRegistry managerComponents, IComponentRegistry serverComponents) throws Throwable {
        serverComponents.addComponent(new AkkaMediator());
        serverComponents.addComponent(new NettyService());
        //先全部增加进去，确保在初始化之前，里面都存在的主键
        serverComponents.forEach( component-> {
            try {
                component.init(wrapper);
            } catch (Throwable throwable) {
                logger.error("{} init error.", component.getName(), throwable);
            }
        });
        managerComponents.addComponent(ClassUtil.newInstance(wrapper.getString(ConfigKey.SERVER_SCODER)));
        managerComponents.forEach( component-> {
            try {
                component.init(wrapper);
            } catch (Throwable throwable) {
                logger.error("{} init error.", component.getName(), throwable);
            }
        });
    }

    private void initConfigComponent(ConfigWrapper wrapper) throws Throwable {
        Map<String,String> configs = YamlUtils.loadConfigIfExits(wrapper.getFilePath("components.yaml"), Map.class);
        if (configs == null || configs.isEmpty()) {
            return;
        }
        for (String clsName: configs.values()) {
            IComponent component = null;
            try {
                component = (IComponent)Class.forName(clsName).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(String.format("初始化组件失败 component:%s ",clsName, e));
            }
            component.init(wrapper);
            this.application.addManager(component);
        }

    }

    private void start(){
        if (running){
            logger.error("server is running now, cann't start again.");
            return;
        }
        running = true;
        application.getAppListener().onLaunch();
    }

    public void stop(){
        application.getAppListener().onShutdown();
    }

    public boolean isRunning() {
        return running;
    }

    public static void main(String[] args) throws Throwable {
        int serverId = Integer.parseInt(args[0]);
        String configPath = args[1];
        AkkaServer server = new AkkaServer(serverId, "name-" +serverId);
        server.init(configPath);
        server.start();
        instance = server;
    }
}
