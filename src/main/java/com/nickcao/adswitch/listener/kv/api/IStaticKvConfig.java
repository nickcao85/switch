package com.nickcao.adswitch.listener.kv.api;

import java.util.Map;

/**
 * Created by caoning on 13/07/2017.
 *
 * @author caoning
 * @date 2017/07/13
 */
public interface IStaticKvConfig {
    Map<String,String> load();
}
