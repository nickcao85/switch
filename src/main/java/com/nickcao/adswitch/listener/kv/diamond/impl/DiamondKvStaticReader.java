package com.nickcao.adswitch.listener.kv.diamond.impl;

import com.google.common.collect.Maps;
import com.nickcao.adswitch.listener.kv.api.IStaticKvConfig;
import com.nickcao.adswitch.listener.kv.diamond.DiamondConfig;
import com.nickcao.adswitch.parser.kv.Text2Kv;
import com.taobao.diamond.client.Diamond;
import com.taobao.diamond.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by caoning on 12/07/2017.
 *
 * @author caoning
 * @date 2017/07/12
 */
public class DiamondKvStaticReader implements IStaticKvConfig, DiamondConfig {
    private String dataId;
    private String groupId;

    public DiamondKvStaticReader(String dataId, String groupId) {
        this.dataId = dataId;
        this.groupId = groupId;
    }

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public Map<String, String> load() {
        try {
            String configString = Diamond.getConfig(this.getDataId(), this.getGroupId(), 10000);
            if(StringUtils.isBlank(configString)){
                return Maps.newHashMap();
            }
            return Text2Kv.getKv(configString);
        }catch(Throwable t){
            logger.error("[Diamond]load log",t);
            return Maps.newHashMap();
        }
    }

    @Override
    public String getDataId() {
        return dataId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }
}
