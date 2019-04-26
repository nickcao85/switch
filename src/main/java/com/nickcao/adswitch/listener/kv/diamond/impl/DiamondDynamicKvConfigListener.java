package com.nickcao.adswitch.listener.kv.diamond.impl;

import com.google.common.collect.Lists;
import com.nickcao.adswitch.api.bo.DiffValue;
import com.nickcao.adswitch.listener.kv.api.IDynamicKvConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by caoning on 01/03/2017.
 * 监听一整套KV配置的变更,回调一个IDynamicKVConfig接口的实现类
 * @author caoning
 * @date 2017/03/01
 */
public class DiamondDynamicKvConfigListener extends DiamondBaseDynamicListener{


    private List<IDynamicKvConfig> instanceList;

    public DiamondDynamicKvConfigListener(String dataId,String groupId) {
        super(dataId,groupId);

    }

    public void rigisterIDynamicKvConfig(IDynamicKvConfig iDynamicKvConfig) {
        List<IDynamicKvConfig> list = Lists.newArrayList();
        list.add(iDynamicKvConfig);
        this.instanceList = list;
    }

    public void rigisterIDynamicKvConfigList(List<IDynamicKvConfig> iDynamicKvConfigList) {
        this.instanceList = iDynamicKvConfigList;
    }



    @Override
    public final void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        if (instanceList != null) {
            for (IDynamicKvConfig dynamicKvConfig : instanceList) {
                dynamicKvConfig.reload(config,diffConfig);
            }
        }
    }
}
