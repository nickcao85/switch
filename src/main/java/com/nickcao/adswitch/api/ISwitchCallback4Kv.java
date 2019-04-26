package com.nickcao.adswitch.api;


import com.nickcao.adswitch.api.bo.DiffValue;

import java.util.Map;

/**
 * Created by caoning on 01/03/2017.
 * 开关回调API
 * @author caoning
 * @date 2017/03/01
 */
public interface ISwitchCallback4Kv {

    /**
     *
     * @param diffConfig 变更，包含增加、删除、变更
     */
    void switchField(Map<String, DiffValue> diffConfig);

}
