package com.ximuyi.core.config;

import java.io.File;
import java.io.IOException;

import com.ximuyi.core.env.EnvKeys;
import com.ximuyi.core.env.EnvUtil;
import com.ximuyi.core.utils.FileUtil;
import jodd.props.Props;

public class Configs {

    private static Configs instance = new Configs();

    public static Configs getInstance() {
        return instance;
    }

    private String rootPath;
    private Props properties;

    private Configs(){
    }

    public void init() throws IOException {
        this.rootPath = EnvUtil.getProperty(EnvKeys.SERVER_HOME);
        File file = FileUtil.scanFileByPath(rootPath, "app.properties");
        this.properties = new Props();
        this.properties.load(file);
    }

    public String getString(String configKey){
        return properties.getBaseValue(configKey);
    }

    public String getString(String configKey, String defaultValue){
        String value =  getString(configKey);
        return value == null ? defaultValue : value;
    }

    public Integer getInteger(String configKey){
        return properties.getIntegerValue(configKey);
    }

    public Integer getInteger(String configKey, int defaultValue){
        Integer value =  getInteger(configKey);
        return value == null ? defaultValue : value;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getFilePath(String filename) {
        return rootPath + File.separator + filename;
    }

    @Override
    public String toString() {
        return "{" +
                "rootPath='" + rootPath + '\'' +
                ", property=" + properties +
                '}';
    }
}
