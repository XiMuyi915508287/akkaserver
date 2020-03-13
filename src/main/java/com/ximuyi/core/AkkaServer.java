package com.ximuyi.core;

import java.lang.reflect.Proxy;
import java.util.Map;

import com.ximuyi.core.api.IAppListener;
import com.ximuyi.core.api.IScheduleManager;
import com.ximuyi.core.api.login.IUserHelper;
import com.ximuyi.core.api.login.ProxyUserHelper;
import com.ximuyi.core.command.handler.CommandHandlerFactory;
import com.ximuyi.core.component.ComponentRegistry;
import com.ximuyi.core.component.IComponent;
import com.ximuyi.core.component.IComponentRegistry;
import com.ximuyi.core.config.ConfigKey;
import com.ximuyi.core.config.Configs;
import com.ximuyi.core.net.netty.NettyService;
import com.ximuyi.core.utils.ClassUtil;
import com.ximuyi.core.utils.YamlUtils;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ximuyi.core.config.ConfigKey.SERVER_APP_LISTENER;
import static com.ximuyi.core.config.ConfigKey.SERVER_SCHEDULE;

public class AkkaServer {

    private static Logger logger = LoggerFactory.getLogger(AkkaServer.class);

    private static AkkaServer instance = null;

    public static AkkaServer getInstance(){
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

    private void init() throws Throwable {
        Configs.getInstance().init();
        ComponentRegistry serverComponents = new ComponentRegistry();
        ComponentRegistry managerComponents = new ComponentRegistry();
        this.application = new AkkaAppContext(appName, managerComponents, serverComponents);
        this.application.setScheduler(createScheduleManager());
        this.application.setAppListener(createAppListener());
        this.application.setUserHelper(createUserHelper());
        this.application.setCommandHandlerFactory(new CommandHandlerFactory());
        ContextResolver.setContext(application);
        CoreAccessor.getInstance().setLocator(new CoreLocatorImpl());
        this.initServerComponent(managerComponents, serverComponents);
        this.initConfigComponent();

        //先初始化自己的组件，在初始化App，不然App获取某些组件会null
        application.getAppListener().onInit();
    }

    private IScheduleManager createScheduleManager(){
        String string = Configs.getInstance().getString(SERVER_SCHEDULE);
        if (StringUtil.isEmpty(string)){
            return new ScheduleManager();
        }
        else {
            return ClassUtil.newInstance(string);
        }
    }

    private IAppListener createAppListener(){
        String string = Configs.getInstance().getString(SERVER_APP_LISTENER);
        if (StringUtil.isEmpty(string)){
            return new AppListener();
        }
        else {
            return ClassUtil.newInstance(string);
        }
    }

    private IUserHelper createUserHelper(){
        String string = Configs.getInstance().getString(ConfigKey.SERVER_LOGIN_HELPER);
        if (StringUtil.isEmpty(string)){
            throw new RuntimeException("配置[" + ConfigKey.SERVER_LOGIN_HELPER + "]不能为空");
        }
        @SuppressWarnings("rawtypes")
        IUserHelper userHelper = ClassUtil.newInstance(string);
        //noinspection rawtypes
        return (IUserHelper) Proxy.newProxyInstance(userHelper.getClass().getClassLoader(), new Class[] { IUserHelper.class }, new ProxyUserHelper(userHelper));
    }

    private void initServerComponent(ComponentRegistry managerComponents, IComponentRegistry serverComponents) throws Throwable {
        serverComponents.addComponent(new AkkaMediator());
        serverComponents.addComponent(new NettyService());
        //先全部增加进去，确保在初始化之前，里面都存在的主键
        serverComponents.forEach( component-> {
            try {
                component.init();
            } catch (Throwable throwable) {
                logger.error("{} init error.", component.getName(), throwable);
            }
        });
        managerComponents.addComponent(ClassUtil.newInstance(Configs.getInstance().getString(ConfigKey.SERVER_CODER)));
        managerComponents.forEach( component-> {
            try {
                component.init();
            } catch (Throwable throwable) {
                logger.error("{} init error.", component.getName(), throwable);
            }
        });
    }

    private void initConfigComponent() throws Throwable {
        String filePath = Configs.getInstance().getConfigFilePath("components.yaml");
        Map<String,String> configs = YamlUtils.loadConfigIfExits(filePath, Map.class);
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
            component.init();
            this.application.addManager(component);
        }

    }

    private void start(){
        if (running){
            logger.error("server is running now, can't start again.");
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
        AkkaServer server = new AkkaServer(serverId, "name-" +serverId);
        server.init();
        server.start();
        instance = server;
    }
}
