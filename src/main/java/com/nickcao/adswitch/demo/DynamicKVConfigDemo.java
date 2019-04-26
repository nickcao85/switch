package com.nickcao.adswitch.demo;


import com.nickcao.adswitch.api.bo.DiffValue;
import com.nickcao.adswitch.listener.kv.api.IDynamicKvConfig;
import com.nickcao.adswitch.listener.kv.diamond.impl.DiamondDynamicKvConfigListener;

import java.util.Map;

/**
 * Created by caoning on 08/02/2017.
 */
public class DynamicKVConfigDemo implements IDynamicKvConfig {
    public static final String DATA_ID = "caoning.dataid";
    public static final String GROUP_ID = "caoning.groupid";


    @Override
    public void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        System.out.println("config:"+config);
        System.out.println("diff-config:"+diffConfig);
    }


    public static void main(String[] args) {
        DynamicKVConfigDemo demo = new DynamicKVConfigDemo();
        DiamondDynamicKvConfigListener listener = new DiamondDynamicKvConfigListener(DATA_ID,GROUP_ID);
        listener.rigisterIDynamicKvConfig(demo);
        listener.rigister2Diamond();
        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
