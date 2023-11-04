package top.meethigher.ftp.server.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Property工具类
 *
 * @author chenchuancheng
 * @since 2023/09/22 12:02
 */

public class BasePropertiesUtils {


    public final static Logger log = LoggerFactory.getLogger(BasePropertiesUtils.class);


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
    public static BaseProperties load(String resource) throws IOException {
        BaseProperties properties = new BaseProperties();
        //优先读取jar包同级配置文件，若不存在，则读取系统内置配置文件
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + resource);
        InputStream is;
        if (file.exists()) {
            is = new FileInputStream(file);
        } else {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        }
        properties.load(is);
        return properties;
    }

    public static List<BaseProperties> load(String parent, String pattern) {
        List<BaseProperties> list = new ArrayList<>();
        try {
            File dir = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + parent);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles((dir1, name) -> name.endsWith(pattern));
                for (File file : files) {
                    BaseProperties properties = new BaseProperties();
                    try (InputStream is = new FileInputStream(file)) {
                        properties.load(is);
                        list.add(properties);
                    }
                }
            } else {
                log.error("{} is not a directory", dir.getAbsolutePath());
            }
        } catch (Exception e
        ) {
            log.error(e.getMessage());
        }
        return list;
    }

}
