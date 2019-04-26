package com.nickcao.adswitch.parser.kv;


import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by caoning on 13/07/2017.
 *
 * @author caoning
 * @date 2017/07/13
 */
public class Text2Kv {
    private static final Logger logger = Logger.getLogger(Text2Kv.class);

    public static Map<String, String> getKv(String contentString) {
        Map<String, String> configMap = new HashMap<>();
        Properties properties = new Properties();
        InputStream input = null;
        try {
            // 超时时间单位：毫秒
            //logger.info("load config from diamond:" + config);
            input = new ByteArrayInputStream(contentString.getBytes());
            properties.load(input);

            Enumeration<?> keys = properties.propertyNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                configMap.put(key, properties.getProperty(key));
            }
            return configMap;

        } catch (Exception e) {
            logger.warn("load from diamond exception.", e);
        }finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
        return configMap;
    }
}
