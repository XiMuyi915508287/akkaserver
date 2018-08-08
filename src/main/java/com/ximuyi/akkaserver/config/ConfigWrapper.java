package com.ximuyi.akkaserver.config;

import com.ximuyi.akkaserver.utils.FileUtil;
import jodd.props.Props;

import java.io.File;
import java.io.IOException;

public class ConfigWrapper {

    private static ConfigWrapper instance;

    public static ConfigWrapper instance() {
        return instance;
    }

    public synchronized static void global(ConfigWrapper wrapper) throws IOException {
        if (instance != null){
            throw new UnsupportedOperationException();
        }
        instance = wrapper;
    }

    private final Props props;
    private final String root;

    private ConfigWrapper(Props props, String root) {
        this.props = props;
        this.root = root;
    }

    public String getString(String configKey){
        return props.getBaseValue(configKey);
    }

    public String getString(String configKey, String defaultValue){
        String value =  getString(configKey);
        return value == null ? defaultValue : value;
    }

    public int getInteger(String configKey){
        return props.getIntegerValue(configKey);
    }

    public int getInteger(String configKey, int defaultValue){
        Integer value =  getInteger(configKey);
        return value == null ? defaultValue : value;
    }

    public String getRoot() {
        return root;
    }

    public String getFilePath(String filename) {
        return root + File.separator + filename;
    }

    public static final ConfigWrapper read(String filepath, String filename) throws IOException {
        File file = FileUtil.scanFileByPath(filepath, filename);
        Props props = new Props();
        props.load(file);
        return new ConfigWrapper(props, file.getParentFile().getAbsolutePath());
    }
}
