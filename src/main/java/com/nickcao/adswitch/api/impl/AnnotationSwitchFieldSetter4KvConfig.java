package com.nickcao.adswitch.api.impl;


import com.nickcao.adswitch.api.annotation.AdStlSwitch;
import com.nickcao.adswitch.api.bo.DiffValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoning on 23/03/2017.
 *
 * @author caoning
 * @date 2017/03/23
 */
public class AnnotationSwitchFieldSetter4KvConfig extends SwitchFieldSetter4KvConfig {

    @Override
    public void switchField(Map<String, DiffValue> diffConfig) {
        if(diffConfig == null || diffConfig.size() == 0) {
            return;
        }
        Map<String,String> field2ConfigKeyMap = new HashMap<>();
        Map<String,Field> fieldName2FieldMap = new HashMap<>();
        Field[] fs = this.getClass().getDeclaredFields();
        if (fs != null && fs.length > 0) {
            for (Field field : fs) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(AdStlSwitch.class)) {

                    continue;
                }
                for (Annotation anno : field.getAnnotations()) {
                    if (!anno.annotationType().equals(AdStlSwitch.class)) {
                        //不是AdStlSwitch的不处理
                        continue;
                    }

                    String configKey = ((AdStlSwitch)anno).configKey();
                    field2ConfigKeyMap.put(field.getName(),configKey);
                }
            }
        }
        super.setFieldName2ConfigKeyMap(field2ConfigKeyMap);

        super.switchField(diffConfig);
    }

}
