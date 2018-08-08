package com.ximuyi.akkaserver.utils;

import jodd.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YamlUtils {

    private static final Logger logger = LoggerFactory.getLogger(YamlUtils.class);

    public static <T> T loadConfigIfExits(String filePath, Class<T> clazz) {
        File file = new File(filePath);
        if (FileUtil.isExistingFile(file)) {
            try {
                return new Yaml().loadAs(new FileInputStream(file), clazz);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(filePath);
            }
        }
        else {
            logger.error("加载可选配置文件失败: {}", filePath);
            return null;
        }
    }

    public static <T> T loadConfig(String filePath, Class<T> clazz) throws FileNotFoundException {
        File file = new File(filePath);
        return new Yaml().loadAs(new FileInputStream(file), clazz);
    }
}
