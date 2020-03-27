package com.pandaroid.dps.singleton.regist;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;

import java.io.Serializable;

public enum EnumSingleton implements Serializable {
    ENUM_SINGLETON_INSTANCE;

    public static EnumSingleton getInstance() {
        return ENUM_SINGLETON_INSTANCE;
    }

    // Start: 需要增加数据对象，供调用获取
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    // End  : 需要增加数据对象，供调用获取
}
