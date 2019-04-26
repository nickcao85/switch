package com.nickcao.adswitch.demo;


import com.nickcao.adswitch.listener.kv.diamond.impl.DiamondKvStaticReader;

import java.util.Map;

/**
 * Created by caoning on 08/02/2017.
 */
public class StaticKVDemo {
    static final String DATA_ID = "caoning.dataid";
    static final String GROUP_ID = "caoning.groupid";

    public static void main(String[] args) {
        DiamondKvStaticReader demo = new DiamondKvStaticReader(DATA_ID,GROUP_ID);
        Map<String, String> result = demo.load();
        System.out.println(result);
    }


}
