package com.nickcao.adswitch.listener.kv.diamond.impl;

import com.dingtalk.chatbot.DingtalkChatbotClient;
import com.dingtalk.chatbot.SendResult;
import com.dingtalk.chatbot.message.TextMessage;
import com.nickcao.adswitch.api.bo.DiffValue;
import com.nickcao.adswitch.listener.kv.api.IDynamicKvConfig;
import com.nickcao.adswitch.listener.kv.diamond.DiamondConfig;
import com.nickcao.adswitch.parser.kv.Text2Kv;
import com.taobao.diamond.client.Diamond;
import com.taobao.diamond.manager.ManagerListenerAdapter;
import com.taobao.diamond.utils.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by caoning on 01/03/2017.
 *
 * @author caoning
 * @date 2017/03/01
 */
public abstract class DiamondBaseDynamicListener extends ManagerListenerAdapter
    implements IDynamicKvConfig, DiamondConfig {

    private String dataId;
    private String groupId;
    private String dingdingURL;

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String loggerPrefix = "[diamond-kv-listener]-";

    private volatile Map<String, String> lastConfigMap = new HashMap<>();

    public DiamondBaseDynamicListener(String dataId, String groupId) {
        this.dataId = dataId;
        this.groupId = groupId;
    }

    public final void rigister2Diamond() {
        Diamond.addListener(this.getDataId(), this.getGroupId(), this);
    }

    @Override
    public void receiveConfigInfo(String configStr) {
        Map<String, String> kvMap = Text2Kv.getKv(configStr);
        logger.info("[Diamond-Change]" + kvMap);
        reload(kvMap);

    }

    public String getDingdingURL() {
        return dingdingURL;
    }

    public void setDingdingURL(String dingdingURL) {
        this.dingdingURL = dingdingURL;
    }

    private void reload(Map<String, String> config) {
        Map<String, DiffValue> dffConfig = calcDiff(config);
        logger.info("[Diamond-CalcDiff]" + dffConfig);
        if (dffConfig != null && dffConfig.size() > 0) {
            try {
                reload(config, dffConfig);
                callDingding(dffConfig);
            } catch (Throwable t) {

            }

        }
    }


    private void callDingding(Map<String, DiffValue> dffConfig) {
        if(StringUtils.isBlank(this.dingdingURL)) return;
        DingtalkChatbotClient client = new DingtalkChatbotClient();
        try {

            InetAddress ia = InetAddress.getLocalHost();
            String ip = ia.getHostAddress();
            String hostName = ia.getHostName();
           String message =
                    "----Diamond变更通知如下---------"+System.lineSeparator()+
                            "Host\t:  "+hostName+System.lineSeparator()+
                            "Time\t:  "+new Date()+System.lineSeparator()+
                            "IP\t:  "+ip+System.lineSeparator()+
                            "Content\t:  "+System.lineSeparator();
           List<String> keyList = new ArrayList<>();
           keyList.addAll(dffConfig.keySet());
           Collections.sort(keyList);
           for(String key : keyList){
               message += "  "+  key +"\t: "+ dffConfig.get(key) +System.lineSeparator();
           }

            TextMessage textMessage = new TextMessage(message);
            textMessage.setIsAtAll(true);

            SendResult result = client.send(this.dingdingURL, textMessage);
            logger.info("[DingDingResult]:"+result);
        } catch (IOException e) {
            //logger.error("",e);
        }
    }

    private Map<String, DiffValue> calcDiff(Map<String, String> newConfig) {

        if (newConfig == null || newConfig.size() == 0) {
            return new HashMap<>(); //无变更
        }

        Set<String> allKeySet = new HashSet<>();
        allKeySet.addAll(newConfig.keySet());
        allKeySet.addAll(this.lastConfigMap.keySet());

        Map<String, DiffValue> diffValueMap = new HashMap<>();

        for (String key : allKeySet) {
            String newValue = StringUtils.defaultIfEmpty(newConfig.get(key), "");
            String oldValue = StringUtils.defaultIfEmpty(this.lastConfigMap.get(key), "");

            //若两个map中相同key对应的value不相等
            if (!newValue.equals(oldValue)) {
                diffValueMap.put(key, new DiffValue(oldValue, newValue));
            }
        }
        this.lastConfigMap = newConfig;
        return diffValueMap;
    }



    public static void main(String[] args) {
        Map<String, DiffValue> dffConfig = new HashMap<>();
        dffConfig.put("Key3",new DiffValue("123","345"));
        dffConfig.put("Key1",new DiffValue("123","345"));
        dffConfig.put("Key5",new DiffValue("123","345"));
        //callDingding(dffConfig);
        new DiamondBaseDynamicListener("123","123") {
            @Override
            public void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {

            }
        }.rigister2Diamond();
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
