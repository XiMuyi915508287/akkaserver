package com.ximuyi.akkaserver;

import com.ximuyi.akkaserver.api.ITaskManager;
import com.ximuyi.akkaserver.session.ISession;

public interface IAkkaUser extends IUser, ISession {

    IUser getExtUser();

    ITaskManager getTaskManager();
}
