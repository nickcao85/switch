package com.nickcao.adswitch.listener.kv.diamond.impl;


import com.nickcao.adswitch.api.bo.DiffValue;
import com.nickcao.adswitch.listener.kv.api.IDynamicKvConfig;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by caoning on 01/03/2017.
 * 提供一套key对应ICallback集合的监听机制,当发生删除、新增、修改时回调ICallback接口的实现类的集合
 * @author caoning
 * @date 2017/03/01
 */
public class DiamondDynamicKvKeyListener extends DiamondBaseDynamicListener {
    private final Logger logger = Logger.getLogger(DiamondDynamicKvKeyListener.class);
    private Map<Set<String>,Set<ICallbackWrapper>> triggerKeySet2CallbackMapper = new ConcurrentHashMap<>();

    public DiamondDynamicKvKeyListener(String dataId, String groupId) {
        super(dataId, groupId);
    }

    //private Map<ICallback,Lock> alterLock = new ConcurrentHashMap<>();



    public void rigisterKeys2Callback(Set<String> keys, IDynamicKvConfig callback){
        Set<ICallbackWrapper> dynamicKVConfigSet = new HashSet<>();
        dynamicKVConfigSet.add(new ICallbackWrapper(callback));
        this.triggerKeySet2CallbackMapper.put(keys,dynamicKVConfigSet);
    }

    public void rigisterKeys2Callback(Set<String> keys,Set<IDynamicKvConfig> callbackSet){
        if(callbackSet!=null && callbackSet.size() >0) {
            Set<ICallbackWrapper> dynamicKVConfigSet = new HashSet<>();

            for (IDynamicKvConfig callback : callbackSet) {
                dynamicKVConfigSet.add(new ICallbackWrapper(callback));
                //this.alterLock.put(callback, new ReentrantLock());
            }
            this.triggerKeySet2CallbackMapper.put(keys, dynamicKVConfigSet);
        }

    }

    @Override
    public final synchronized void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        logger.info("trigger start ... ");
        if(diffConfig == null){
            return ;
        }
        for(String diffKey : diffConfig.keySet()){
            //Lock callbackActionLock = this.alterLock.get(diffKey);
            try {
                //callbackActionLock.lock();
                //callback
                Set<Set<String>> allTriggerKeySets = this.triggerKeySet2CallbackMapper.keySet();
                logger.info("trigger:" + allTriggerKeySets);
                for(Set<String> triggerKeySet : allTriggerKeySets){
                    if(triggerKeySet!=null && triggerKeySet.contains(diffKey)){
                        Set<ICallbackWrapper> callbackSet = this.triggerKeySet2CallbackMapper.get(triggerKeySet);
                        if(callbackSet!=null && callbackSet.size()>0){
                            for(ICallbackWrapper callbackWrapper : callbackSet){
                                if(callbackWrapper.isTriggered()) {
                                    continue;
                                }
                                try {
                                    if (callbackWrapper.getInstance() != null) {
                                        logger.info("triggered ... " + callbackWrapper);
                                        callbackWrapper.getInstance().reload(config, diffConfig);
                                    }
                                    callbackWrapper.setTriggered(true);
                                } catch (Throwable t) {
                                    this.logger.error("[reload-log]", t);
                                }

                            }
                        }
                    }
                }

            }finally {
                //unlock
                //callbackActionLock.unlock();
            }


        }

        //重置所有调用状态
        for(String diffKey : diffConfig.keySet()){
            //Lock callbackActionLock = this.alterLock.get(diffKey);
            try {
                //callbackActionLock.lock();
                //callback
                Set<Set<String>> allTriggerKeySets = this.triggerKeySet2CallbackMapper.keySet();
                for(Set<String> triggerKeySet: allTriggerKeySets){
                    if(triggerKeySet!=null && triggerKeySet.contains(diffKey)){
                        Set<ICallbackWrapper> callbackSet = this.triggerKeySet2CallbackMapper.get(triggerKeySet);
                        if(callbackSet!=null && callbackSet.size()>0){
                            for(ICallbackWrapper callbackWrapper : callbackSet){
                                callbackWrapper.setTriggered(false);

                            }
                        }
                    }
                }

            }finally {
                //unlock
                //callbackActionLock.unlock();
            }


        }
    }


    private static class ICallbackWrapper{
        private volatile boolean triggered = false;
        private IDynamicKvConfig instance;

        public ICallbackWrapper(IDynamicKvConfig instance) {
            this.instance = instance;
        }

        public void setTriggered(boolean triggered) {
            this.triggered = triggered;
        }

        public void setInstance(IDynamicKvConfig instance) {
            this.instance = instance;
        }

        public boolean isTriggered() {
            return triggered;
        }

        public IDynamicKvConfig getInstance() {
            return instance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ICallbackWrapper)) {
                return false;
            }

            ICallbackWrapper that = (ICallbackWrapper) o;

            return getInstance() != null ? getInstance().equals(that.getInstance()) : that.getInstance() == null;
        }

        @Override
        public int hashCode() {
            return getInstance() != null ? getInstance().hashCode() : 0;
        }
    }


}
