package com.ximuyi.akkaserver.extension;

public class CommandUtil {

    public static final boolean equals(ICommand command0, ICommand command1){
        return command0.getUniqueId() == command1.getUniqueId();
    }
}
