package com.app.greatriverma.model;

/**
 * Created by Engr G M on 9/3/2017.
 */

public class EnvirFieldBean {

    public String key;
    public String value;
    public String radioValue;//n/a = 2, yes = 1, 0 = no

    public EnvirFieldBean(String key, String value, String radioValue) {
        this.key = key;
        this.value = value;
        this.radioValue = radioValue;
    }
}
