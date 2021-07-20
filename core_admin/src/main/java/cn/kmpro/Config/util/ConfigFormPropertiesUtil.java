package cn.kmpro.Config.util;


import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @desc  加载properties的配置项
 */
@Component
public class ConfigFormPropertiesUtil {

    private static Properties properties;

    static {
        properties = new Properties();
        String fileName = "kmproconfig/core.properties";
        InputStream in = ConfigFormPropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 根据key，获取到相对应的Propertie。
     * @param key
     * @return
     */
    public static Object getContextPropertie(String key){
        return properties.get(key);
    }

}
