package top.meethigher.ftp.server.ftpserver.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

/**
 * Property工具类
 *
 * @author chenchuancheng
 * @since 2023/09/22 12:02
 */
@Slf4j
public class PropertiesUtils {


    /**
     * 解析properties
     * 优先加载jar包同级
     * 若不存在，则加载内置
     *
     * @param resource 配置文件
     * @return map
     */
    public static Map<String, String> loadToMap(String resource) throws IOException {
        Properties properties = load(resource);
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            map.put(key, value);
        }
        return map;
    }


    /**
     * 解析properties
     * 优先加载jar包同级
     * 若不存在，则加载内置
     *
     * @param resource 配置文件
     * @return map
     */
    public static Properties load(String resource) throws IOException {
        Properties properties = new Properties();
        //优先读取jar包同级配置文件，若不存在，则读取系统内置配置文件
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + resource);
        InputStream is;
        if (file.exists()) {
            is = new FileInputStream(file);
            log.info("加载外部配置文件 {}", resource);
        } else {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            log.debug("加载内置配置文件 {}", resource);
        }
        properties.load(is);
        return properties;
    }

    public static List<Properties> load(String parent, String pattern) {
        List<Properties> list = new ArrayList<>();
        try {
            File dir = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + parent);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles((dir1, name) -> name.endsWith(pattern));
                for (File file : files) {
                    Properties properties = new Properties();
                    try (InputStream is = new FileInputStream(file)) {
                        properties.load(is);
                        list.add(properties);
                    }
                }
            } else {
                log.error("{} 不是一个目录", dir.getAbsolutePath());
            }
        } catch (Exception e
        ) {
            log.error(e.getMessage());
        }
        return list;

    }

}
