package com.nickcao.adswitch.listener.kv.api;


import com.nickcao.adswitch.api.bo.DiffValue;

import java.util.Map;

/**
 * Created by caoning on 13/07/2017.
 *
 * @author caoning
 * @date 2017/07/13
 */
public interface IDynamicKvConfig {
    /**
     *
     * @param config diamond上最新的配置转化的KV map
     * @param diffConfig 变更，包含增加、删除、变更
     */
    void reload(Map<String, String> config, Map<String, DiffValue> diffConfig);
}
