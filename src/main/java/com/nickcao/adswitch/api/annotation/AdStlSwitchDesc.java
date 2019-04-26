package com.nickcao.adswitch.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by caoning on 23/03/2017.
 * @author caoning
 * @date 2017/03/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdStlSwitchDesc {
}
