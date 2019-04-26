package com.nickcao.adswitch.api.bo;

/**
 * Created by caoning on 01/03/2017.
 *
 * @author caoning
 * @date 2017/03/01
 */
public class DiffValue {
    //启动时，oldValue是空串，例如：{"oldValue":"", "newValue":"1"}
    private String oldValue;
    private String newValue;

    public DiffValue(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "{\"DiffValue\":{"
                + "\"oldValue\":\"" + oldValue + "\""
                + ", \"newValue\":\"" + newValue + "\""
                + "}}";
    }
}
