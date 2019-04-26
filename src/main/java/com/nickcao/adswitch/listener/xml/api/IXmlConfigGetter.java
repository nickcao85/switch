package com.nickcao.adswitch.listener.xml.api;

/**
 * Created by caoning on 13/07/2017.
 * 从XML转为对象
 * @author caoning
 * @date 2017/07/13
 */
public interface IXmlConfigGetter<T> {
    T load();
}
