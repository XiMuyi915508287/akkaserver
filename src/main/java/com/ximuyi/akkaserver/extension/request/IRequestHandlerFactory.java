package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.ICommand;

public interface IRequestHandlerFactory {

    void reload(IRequestHandler handler);

    IRequestHandler find(ICommand cmd);
}
