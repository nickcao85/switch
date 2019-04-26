package com.nickcao.adswitch.api.impl;


import com.nickcao.adswitch.api.ISwitchCallback4Kv;
import com.nickcao.adswitch.api.annotation.AdStlSwitch;
import com.nickcao.adswitch.api.annotation.AdStlSwitchDesc;
import com.nickcao.adswitch.api.bo.DiffValue;
import com.nickcao.adswitch.listener.kv.api.IDynamicKvConfig;
import com.taobao.diamond.utils.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoning on 23/03/2017.
 *
 * @author caoning
 * @date 2017/03/23
 */
public class SwitchFieldSetter4KvConfig implements ISwitchCallback4Kv, IDynamicKvConfig {
    private Logger logger = Logger.getLogger(this.getClass());
    private Map<String,String> fieldName2ConfigKeyMap = new HashMap<>();
    private Map<String,Field> fieldName2FieldMap = new HashMap<>();

    public SwitchFieldSetter4KvConfig(){
        logger.info("[switch]from constructor");
        this.initFieldName2FieldMap();

    }

    public void initFieldName2FieldMap(){
        logger.info("[switch]from initFieldName2FieldMap");
        Field[] fs = this.getClass().getDeclaredFields();
        logger.info("[switch]fs size:"+fs.length);
        if (fs != null && fs.length > 0) {
            for (Field field : fs) {
                field.setAccessible(true);

                if (!field.isAnnotationPresent(AdStlSwitchDesc.class) && !field.isAnnotationPresent(AdStlSwitch.class) ) {
                    logger.info("[switch]:fieldName:"+field.getName()+" filtered");
                    continue;
                }
                logger.info("[switch]:fieldName:"+field.getName()+" putted in map");
                this.fieldName2FieldMap.put(field.getName(),field);
            }
        }
        logger.info("[switch]:fieldName2FieldMap:"+fieldName2FieldMap);
    }

    public void setFieldName2ConfigKeyMap(Map<String, String> fieldName2ConfigKeyMap) {
        this.fieldName2ConfigKeyMap = fieldName2ConfigKeyMap;
    }

    @Override
    public void switchField(Map<String, DiffValue> diffConfig) {

        if(diffConfig == null || diffConfig.size() == 0) {
            return;
        }

        for (String fieldName : this.fieldName2ConfigKeyMap.keySet()) {
            String configKey = fieldName2ConfigKeyMap.get(fieldName);

            if(StringUtils.isBlank(configKey)) {
                return;
            }
            Field field = this.fieldName2FieldMap.get(fieldName);
            if(field == null){
                return;
            }

            DiffValue diffValue = diffConfig.get(configKey);
            String newValue = diffValue == null ? null : diffValue.getNewValue();
            if (newValue != null) {
                //得到此属性的类型
                String type = field.getType().toString();
                type = type.toLowerCase();
                try {
                    if (type.endsWith("string")) {
                        field.set(this, newValue);
                    } else if (type.endsWith("int") || type.endsWith("integer")) {
                        field.set(this, Integer.parseInt(newValue));
                    } else if (type.endsWith("long")) {
                        field.set(this, Long.parseLong(newValue));
                    } else if (type.endsWith("boolean")) {
                        field.set(this, Boolean.parseBoolean(newValue));
                    } else if (type.endsWith("double")) {
                        field.set(this, Double.parseDouble(newValue));
                    } else if (type.endsWith("float")) {
                        field.set(this, Float.parseFloat(newValue));
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e);
                }
                logger.info("[switch]"+this.getClass().getName()+"|fieldName:"+fieldName+"|new value is:"+newValue);
            }
        }
    }



    @Override
    public void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        this.switchField(diffConfig);
        logger.info("[switch]:fieldName2ConfigKeyMap:"+fieldName2ConfigKeyMap+"|fieldName2FieldMap:"+fieldName2FieldMap);
    }

    @Override
    public String toString() {
        return "{\"SwitchFieldSetter4KvConfig\":{"
                + "\"fieldName2ConfigKeyMap\":" + fieldName2ConfigKeyMap
                + ", \"fieldName2FieldMap\":" + fieldName2FieldMap
                + "}}";
    }
}
