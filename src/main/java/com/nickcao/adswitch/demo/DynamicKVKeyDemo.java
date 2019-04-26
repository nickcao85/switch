package com.nickcao.adswitch.demo;


import com.nickcao.adswitch.listener.kv.diamond.impl.DiamondDynamicKvKeyListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by caoning on 08/02/2017.
 */
public class DynamicKVKeyDemo {
    public static final String DATA_ID = "key-callback";
    public static final String GROUP_ID = "caoning.groupid";



    public static void main(String[] args) {
        //DynamicKeyDemo demo = new DynamicKeyDemo();
        DiamondDynamicKvKeyListener listener = new DiamondDynamicKvKeyListener(DATA_ID,GROUP_ID) ;


        Set<String> listenerKeySet1 = new HashSet<>();
        listenerKeySet1.add("key1");
        listenerKeySet1.add("key2");
        listenerKeySet1.add("key3");

        listener.rigisterKeys2Callback(listenerKeySet1,
                (config, diffConfig) -> System.out.println("callback1----diffConfig:"+diffConfig));

        Set<String> listenerKeySet2 = new HashSet<>();
        listenerKeySet2.add("key2");
        listenerKeySet2.add("key3");
        listenerKeySet2.add("key4");

        listener.rigisterKeys2Callback(listenerKeySet2,
                (config, diffConfig) -> System.out.println("callback2------diffConfig:"+diffConfig));


        listener.rigister2Diamond();


        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
