package com.ximuyi.akkaserver.core;

public class CoreAccessor {

    private static CoreLocator locator;

    protected static synchronized void setLocator(CoreLocator myLocator) {
        if (locator != null){
            throw new UnsupportedOperationException();
        }
        locator = myLocator;
    }

    public static CoreLocator getLocator() {
        return locator;
    }
}
