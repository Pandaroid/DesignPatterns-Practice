package com.pandaroid.dps.singleton.serializable;

import com.pandaroid.dps.singleton.lazy.ILazySingleton;

import java.io.Serializable;

public class SerializableSingleton implements Serializable, ILazySingleton {

    private SerializableSingleton() {
        if(null != SerializableSingleton.LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance) {
            throw new RuntimeException("[SerializableSingleton] 单例类只能创建一个实例，不允许创建多个实例");
        }
    }

    public static SerializableSingleton getInstance() {
        return SerializableSingleton.LazyInnerClassSingletonInstanceHolder.lazyInnerClassSingletonInstance;
    }

    private static class LazyInnerClassSingletonInstanceHolder {
        private static final SerializableSingleton lazyInnerClassSingletonInstance;
        static {
            lazyInnerClassSingletonInstance  = new SerializableSingleton();
        }
    }

    // 通过 readResolve 解决序列化破坏单例的问题
    private Object readResolve() {
        return getInstance();
    }
}
