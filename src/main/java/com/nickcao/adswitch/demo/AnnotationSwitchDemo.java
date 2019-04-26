package com.nickcao.adswitch.demo;


import com.nickcao.adswitch.api.annotation.AdStlSwitch;
import com.nickcao.adswitch.api.impl.AnnotationSwitchFieldSetter4KvConfig;
import com.nickcao.adswitch.listener.kv.diamond.impl.DiamondDynamicKvKeyListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by caoning on 23/03/2017.
 *
 * @author caoning
 * @date 2017/03/23
 */
public class AnnotationSwitchDemo {

    public static final String DATA_ID = "caoning.dataid";
    public static final String GROUP_ID = "caoning.groupid";

    static class MyClass extends AnnotationSwitchFieldSetter4KvConfig {
        @AdStlSwitch(configKey = "haha")
        private int value = 111;

        @Override
        public String toString() {
            return "{\"MyClass\":"
                + super.toString()
                + ", \"value\":\"" + value + "\""
                + "}";
        }
    }

    public static void main(String[] args) {
        MyClass myClass =  new MyClass();
        DiamondDynamicKvKeyListener listener = new DiamondDynamicKvKeyListener(DATA_ID,GROUP_ID);


        Set<String> listenerKeySet1 = new HashSet<>();
        listenerKeySet1.add("haha");

        listener.setDingdingURL("http://创建一个机器人回调url");
        listener.rigisterKeys2Callback(listenerKeySet1,myClass);
        listener.rigister2Diamond();

        new Thread(){
            public void run(){
                while(true) {
                    System.out.println(myClass);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
