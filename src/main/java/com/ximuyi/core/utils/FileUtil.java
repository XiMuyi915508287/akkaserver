package com.ximuyi.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.google.common.collect.Lists;
import jodd.io.findfile.FindFile;

public class FileUtil {

    /**
     * 查找文件.
     *
     * @param searchPath            搜索路径
     * @param IncludeDirs            是否包含子文件夹
     * @return the list
     */
    public static List<File> scanPath(String searchPath, boolean IncludeDirs) {
        List<File> result = Lists.newArrayList();
        FindFile ff = new FindFile().recursive(true).includeDirs(IncludeDirs).searchPath(searchPath);
        File f;
        while ((f = ff.nextFile()) != null) {
            result.add(f);
        }
        return result;
    }

    /**
     * 查找文件(不包含子文件夹).
     *
     * @param searchPath            搜索路径
     * @return the list
     */
    public static List<File> scanPath(String searchPath) {
        return scanPath(searchPath, false);
    }

    /**
     * 查找文件在指定目录下.
     *
     * @param searchPath the search path
     * @param fileName the file name
     * @return the file
     * @throws FileNotFoundException the file not exit exception
     */
    public static File scanFileByPath(String searchPath, String fileName) throws FileNotFoundException {
        List<File> scanPath = scanPath(searchPath, false);
        for (File file : scanPath) {
            if (file.getName().equals(fileName)) { return file; }
        }
        throw new FileNotFoundException(fileName);
    }
}
